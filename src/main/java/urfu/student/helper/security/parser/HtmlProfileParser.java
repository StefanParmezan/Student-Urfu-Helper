package urfu.student.helper.security.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import urfu.student.helper.models.student.Student;
import urfu.student.helper.models.course.Course;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class HtmlProfileParser {

    private static final Logger logger = LoggerFactory.getLogger(HtmlProfileParser.class);

    // Паттерн для декодирования email из HTML entities
    private static final Pattern EMAIL_PATTERN = Pattern.compile("mailto:([^\"]+)");

    public Student parseStudentProfile(String html, String plainPassword) {
        logger.info("Starting HTML profile parsing");

        if (html == null || html.trim().isEmpty()) {
            logger.error("HTML content is null or empty");
            throw new IllegalArgumentException("HTML content cannot be null or empty");
        }

        Document doc = Jsoup.parse(html);
        Student student = new Student();

        try {
            // Проверяем, что это действительно страница профиля УрФУ
            if (!isValidUrfuProfilePage(doc)) {
                logger.error("Invalid URFU profile page - may be login failed or wrong page");
                throw new RuntimeException("Invalid URFU profile page - authentication may have failed");
            }

            // Парсинг ФИО из реальной структуры УрФУ
            parseFullNameFromUrfu(doc, student);

            // Парсинг email из реальной структуры
            parseEmailFromUrfu(doc, student);

            // Парсинг остальных полей из реальной структуры
            parseStudentDetailsFromUrfu(doc, student);

            // Парсинг курсов из реальной структуры
            parseCoursesFromUrfu(doc, student);

            // Устанавливаем пароль
            student.setPassword(plainPassword);

            logger.info("Successfully parsed URFU student profile for: {} {}",
                    student.getStudentSurName(), student.getStudentName());

        } catch (Exception e) {
            logger.error("Error parsing URFU student profile HTML", e);
            throw new RuntimeException("Failed to parse URFU student profile: " + e.getMessage(), e);
        }

        return student;
    }

    private boolean isValidUrfuProfilePage(Document doc) {
        // Проверяем наличие ключевых элементов профиля УрФУ
        boolean hasUrfuHeader = doc.selectFirst(".header-main") != null;
        boolean hasPageHeader = doc.selectFirst("#page-header") != null;
        boolean hasProfileTree = doc.selectFirst(".profile_tree") != null;
        boolean hasUserInfo = doc.selectFirst("dt:contains(Адрес электронной почты)") != null;

        logger.debug("URFU Profile validation - hasUrfuHeader: {}, hasPageHeader: {}, hasProfileTree: {}, hasUserInfo: {}",
                hasUrfuHeader, hasPageHeader, hasProfileTree, hasUserInfo);

        return hasUrfuHeader && hasPageHeader && hasProfileTree && hasUserInfo;
    }

    private void parseFullNameFromUrfu(Document doc, Student student) {
        try {
            // В реальном HTML УрФУ ФИО находится в нескольких местах, попробуем разные варианты

            // Способ 1: Из заголовка страницы
            Element nameElement = doc.selectFirst("h1.h2");
            if (nameElement != null) {
                parseNameFromElement(nameElement, student);
                return;
            }

            // Способ 2: Из title страницы
            Element titleElement = doc.selectFirst("title");
            if (titleElement != null) {
                String title = titleElement.text();
                if (title.contains(":")) {
                    String nameFromTitle = title.split(":")[0].trim();
                    parseNameFromString(nameFromTitle, student);
                    return;
                }
            }

            // Способ 3: Из page-header-headings
            Element pageHeader = doc.selectFirst(".page-header-headings h1");
            if (pageHeader != null) {
                parseNameFromElement(pageHeader, student);
                return;
            }

            logger.warn("Could not find student name in URFU profile");

        } catch (Exception e) {
            logger.error("Error parsing full name from URFU profile", e);
        }
    }

    private void parseNameFromElement(Element element, Student student) {
        String fullName = element.text().trim();
        parseNameFromString(fullName, student);
    }

    private void parseNameFromString(String fullName, Student student) {
        logger.debug("Parsing name from string: {}", fullName);

        if (fullName.isEmpty()) {
            logger.warn("Empty full name");
            return;
        }

        // Разбиваем ФИО на части (Фамилия Имя Отчество)
        String[] nameParts = fullName.split("\\s+");

        // Логика разбора ФИО
        if (nameParts.length >= 3) {
            student.setStudentSurName(nameParts[0]);
            student.setStudentName(nameParts[1]);
            student.setPatronymic(nameParts[2]);
        } else if (nameParts.length == 2) {
            student.setStudentSurName(nameParts[0]);
            student.setStudentName(nameParts[1]);
            student.setPatronymic(""); // нет отчества
        } else if (nameParts.length == 1) {
            student.setStudentSurName(nameParts[0]);
            student.setStudentName("");
            student.setPatronymic("");
        }

        logger.debug("Parsed name - Surname: '{}', Name: '{}', Patronymic: '{}'",
                student.getStudentSurName(), student.getStudentName(), student.getPatronymic());
    }

    private void parseEmailFromUrfu(Document doc, Student student) {
        try {
            // В реальном HTML УрФУ email может быть в разных местах

            // Способ 1: Ищем по тексту "Адрес электронной почты"
            String email = findEmailByLabel(doc, "Адрес электронной почты");
            if (email != null) {
                student.setEmail(email);
                return;
            }

            // Способ 2: Ищем любую ссылку mailto в секциях профиля
            Elements mailtoLinks = doc.select(".profile_tree a[href^=mailto]");
            for (Element link : mailtoLinks) {
                String href = link.attr("href");
                String foundEmail = decodeEmailFromHref(href);
                if (foundEmail != null && !foundEmail.isEmpty()) {
                    student.setEmail(foundEmail);
                    logger.debug("Found email from mailto link: {}", foundEmail);
                    return;
                }
            }

            logger.warn("Email not found in URFU profile");

        } catch (Exception e) {
            logger.error("Error parsing email from URFU profile", e);
        }
    }

    private String findEmailByLabel(Document doc, String label) {
        try {
            // Ищем элемент dt с нужным текстом
            Elements labelElements = doc.select("dt");
            for (Element labelElement : labelElements) {
                if (labelElement.text().contains(label)) {
                    Element parent = labelElement.parent();
                    if (parent != null) {
                        Element emailLink = parent.selectFirst("a[href^=mailto]");
                        if (emailLink != null) {
                            String href = emailLink.attr("href");
                            return decodeEmailFromHref(href);
                        }

                        // Если нет ссылки, может быть просто текст
                        Element ddElement = labelElement.nextElementSibling();
                        if (ddElement != null) {
                            String text = ddElement.text();
                            // Попробуем извлечь email из текста
                            Pattern emailPattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
                            Matcher matcher = emailPattern.matcher(text);
                            if (matcher.find()) {
                                return matcher.group();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error finding email by label: {}", label, e);
        }

        return null;
    }

    private String decodeEmailFromHref(String href) {
        if (href == null || href.isEmpty()) {
            return null;
        }

        try {
            // Извлекаем email из mailto:encoded-email
            Matcher matcher = EMAIL_PATTERN.matcher(href);
            if (matcher.find()) {
                String encodedEmail = matcher.group(1);
                // Декодируем HTML entities и URL encoding
                String decodedEmail = Jsoup.parse(encodedEmail).text();
                // Дополнительное декодирование URL если нужно
                decodedEmail = java.net.URLDecoder.decode(decodedEmail, "UTF-8");
                return decodedEmail;
            }
        } catch (Exception e) {
            logger.error("Error decoding email from href: {}", href, e);
        }

        return null;
    }

    private void parseStudentDetailsFromUrfu(Document doc, Student student) {
        // Парсим различные поля из реальной структуры УрФУ
        findAndSetDetail(doc, "Часовой пояс", student::setTimeZone);
        findAndSetDetail(doc, "Должность", student::setEducationStatus);
        findAndSetDetail(doc, "Academic_group", student::setAcademicGroup);
        findAndSetDetail(doc, "Student_number", student::setStudentNumber);

        logger.debug("Parsed URFU student details - timeZone: '{}', educationStatus: '{}', academicGroup: '{}', studentNumber: '{}'",
                student.getTimeZone(), student.getEducationStatus(),
                student.getAcademicGroup(), student.getStudentNumber());
    }

    private void findAndSetDetail(Document doc, String label, java.util.function.Consumer<String> setter) {
        try {
            Elements labelElements = doc.select("dt");
            for (Element labelElement : labelElements) {
                if (labelElement.text().contains(label)) {
                    Element ddElement = labelElement.nextElementSibling();
                    if (ddElement != null) {
                        String value = ddElement.text().trim();
                        if (!value.isEmpty()) {
                            setter.accept(value);
                            logger.debug("Found detail '{}': '{}'", label, value);
                            return;
                        }
                    }
                }
            }
            logger.debug("Detail '{}' not found in URFU profile", label);
        } catch (Exception e) {
            logger.error("Error parsing detail: {}", label, e);
        }
    }

    private void parseCoursesFromUrfu(Document doc, Student student) {
        try {
            logger.debug("Starting course parsing from URFU profile");

            List<Course> courses = new ArrayList<>();

            // Способ 1: Ищем курсы в секции "Информация о курсах"
            Elements courseSections = doc.select("section.node_category");
            for (Element section : courseSections) {
                Element heading = section.selectFirst("h3.lead");
                if (heading != null && heading.text().contains("Информация о курсах")) {
                    logger.debug("Found courses section with heading: {}", heading.text());

                    // Ищем ссылки на курсы внутри этой секции
                    Elements courseLinks = section.select("a[href*=/course/]");
                    if (!courseLinks.isEmpty()) {
                        logger.debug("Found {} course links in courses section", courseLinks.size());
                        for (Element link : courseLinks) {
                            Course course = createCourseFromLink(link, student);
                            if (course != null) {
                                courses.add(course);
                            }
                        }
                    } else {
                        logger.debug("No course links found in courses section, trying alternative parsing");
                        // Альтернативный способ: ищем в списках
                        Elements listItems = section.select("li");
                        for (Element listItem : listItems) {
                            Element link = listItem.selectFirst("a[href*=/course/]");
                            if (link != null) {
                                Course course = createCourseFromLink(link, student);
                                if (course != null) {
                                    courses.add(course);
                                }
                            }
                        }
                    }
                    break;
                }
            }

            // Способ 2: Ищем курсы по dt "Участник курсов"
            if (courses.isEmpty()) {
                logger.debug("Trying alternative course parsing by dt label");
                Element coursesDt = doc.selectFirst("dt:contains(Участник курсов)");
                if (coursesDt != null) {
                    logger.debug("Found courses dt element");
                    Element parent = coursesDt.parent();
                    if (parent != null) {
                        // Ищем все ссылки на курсы в родительском элементе
                        Elements courseLinks = parent.select("a[href*=/course/]");
                        logger.debug("Found {} course links in dt parent", courseLinks.size());

                        for (Element link : courseLinks) {
                            Course course = createCourseFromLink(link, student);
                            if (course != null) {
                                courses.add(course);
                            }
                        }
                    }
                }
            }

            // Способ 3: Ищем любые ссылки на курсы во всем профиле
            if (courses.isEmpty()) {
                logger.debug("Trying global course link search");
                Elements allCourseLinks = doc.select(".profile_tree a[href*=/course/]");
                logger.debug("Found {} course links in entire profile", allCourseLinks.size());

                for (Element link : allCourseLinks) {
                    Course course = createCourseFromLink(link, student);
                    if (course != null) {
                        courses.add(course);
                    }
                }
            }

            student.setCourseList(courses);
            logger.info("Successfully parsed {} URFU courses", courses.size());

        } catch (Exception e) {
            logger.error("Error parsing URFU courses", e);
            student.setCourseList(new ArrayList<>()); // устанавливаем пустой список в случае ошибки
        }
    }

    private Course createCourseFromLink(Element link, Student student) {
        try {
            String courseName = link.text().trim();
            String courseUrl = link.attr("href");

            if (!courseName.isEmpty() && !courseName.matches("https?://.*")) {
                Course course = new Course();
                course.setCourseName(courseName);

                // Обеспечиваем полный URL если нужно
                if (courseUrl.startsWith("/")) {
                    courseUrl = "https://elearn.urfu.ru" + courseUrl;
                } else if (!courseUrl.startsWith("http")) {
                    courseUrl = "https://elearn.urfu.ru/" + courseUrl;
                }
                course.setCourseUrl(courseUrl);

                course.setStudent(student);

                logger.trace("Parsed URFU course: {} -> {}", courseName, courseUrl);
                return course;
            }
        } catch (Exception e) {
            logger.error("Error creating course from link", e);
        }
        return null;
    }

    /**
     * Вспомогательный метод для проверки, содержит ли HTML сообщение об ошибке аутентификации
     */
    public boolean containsAuthError(String html) {
        if (html == null) return true;

        Document doc = Jsoup.parse(html);

        // Проверяем наличие форм логина или сообщений об ошибках
        boolean hasLoginForm = doc.selectFirst("form[id=login]") != null;
        boolean hasErrorMessages = doc.text().toLowerCase().contains("неверный логин") ||
                doc.text().toLowerCase().contains("invalid login") ||
                doc.text().toLowerCase().contains("ошибка аутентификации") ||
                doc.text().toLowerCase().contains("неправильный логин") ||
                doc.text().toLowerCase().contains("неверные учетные данные");

        logger.debug("URFU Auth error check - hasLoginForm: {}, hasErrorMessages: {}", hasLoginForm, hasErrorMessages);

        return hasLoginForm || hasErrorMessages;
    }

    /**
     * Метод для диагностики структуры HTML (полезен для отладки)
     */
    public void diagnoseHtmlStructure(String html) {
        logger.info("=== HTML STRUCTURE DIAGNOSIS ===");

        Document doc = Jsoup.parse(html);

        // Анализируем заголовки
        Elements h1Elements = doc.select("h1");
        logger.info("Found {} h1 elements:", h1Elements.size());
        for (Element h1 : h1Elements) {
            logger.info("  h1: '{}' (class: {})", h1.text(), h1.attr("class"));
        }

        // Анализируем заголовки h2
        Elements h2Elements = doc.select("h2");
        logger.info("Found {} h2 elements:", h2Elements.size());
        for (Element h2 : h2Elements) {
            logger.info("  h2: '{}' (class: {})", h2.text(), h2.attr("class"));
        }

        // Анализируем dt элементы (definition terms)
        Elements dtElements = doc.select("dt");
        logger.info("Found {} dt elements:", dtElements.size());
        for (Element dt : dtElements) {
            logger.info("  dt: '{}'", dt.text());
            Element dd = dt.nextElementSibling();
            if (dd != null) {
                logger.info("    dd: '{}'", dd.text());
                // Для "Участник курсов" выводим дополнительную информацию
                if (dt.text().contains("Участник курсов")) {
                    logger.info("    dd HTML: {}", dd.html());
                    // Ищем ссылки на курсы в этом dd
                    Elements courseLinks = dd.select("a[href*=/course/]");
                    logger.info("    Found {} course links in this dd:", courseLinks.size());
                    for (Element link : courseLinks) {
                        logger.info("      course link: '{}' -> '{}'", link.text(), link.attr("href"));
                    }
                }
            }
        }

        // Анализируем mailto ссылки
        Elements mailtoLinks = doc.select("a[href^=mailto]");
        logger.info("Found {} mailto links:", mailtoLinks.size());
        for (Element link : mailtoLinks) {
            logger.info("  mailto: '{}' -> '{}'", link.text(), link.attr("href"));
        }

        // Анализируем ссылки на курсы во всем документе
        Elements courseLinks = doc.select("a[href*=/course/]");
        logger.info("Found {} course links in entire document:", courseLinks.size());
        for (Element link : courseLinks) {
            logger.info("  course: '{}' -> '{}'", link.text(), link.attr("href"));
        }

        // Анализируем секции профиля
        Elements profileSections = doc.select("section.node_category");
        logger.info("Found {} profile sections:", profileSections.size());
        for (Element section : profileSections) {
            Element heading = section.selectFirst("h3.lead");
            if (heading != null) {
                logger.info("  section: '{}'", heading.text());
                // Для секции с курсами выводим дополнительную информацию
                if (heading.text().contains("Информация о курсах")) {
                    logger.info("    section HTML: {}", section.html());
                    Elements sectionCourseLinks = section.select("a[href*=/course/]");
                    logger.info("    Found {} course links in this section:", sectionCourseLinks.size());
                    for (Element link : sectionCourseLinks) {
                        logger.info("      course: '{}' -> '{}'", link.text(), link.attr("href"));
                    }
                }
            }
        }

        logger.info("=== END DIAGNOSIS ===");
    }
}