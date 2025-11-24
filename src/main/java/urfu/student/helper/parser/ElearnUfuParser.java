package urfu.student.helper.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import urfu.student.helper.db.course.dto.CourseAiDTO;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ElearnUfuParser {

    private static final Logger logger = LoggerFactory.getLogger(ElearnUfuParser.class);

    private final RestClient restClient;

    public ElearnUfuParser() {
        this.restClient = RestClient.builder()
                .baseUrl("https://elearn.urfu.ru")
                .build();
        logger.info("Инициализирован парсер для elearn.urfu.ru");
    }

    public TreeSet<CourseAiDTO> parseAllCourses() {
        logger.info("Начало парсинга всех курсов с портала электронного обучения");

        TreeSet<CourseAiDTO> courses = new TreeSet<>();
        long startTime = System.currentTimeMillis();

        try {
            // Парсим основную страницу с категориями
            logger.debug("Загрузка основной страницы с категориями");
            String mainPageContent = fetchPageContent("/course/index.php?browse=categories&page=1&categoryid=0");

            if (mainPageContent != null) {
                logger.debug("Основная страница загружена успешно, начинаем парсинг");
                int beforeMainParse = courses.size();
                parseCoursesFromPage(mainPageContent, courses);
                int parsedFromMain = courses.size() - beforeMainParse;
                logger.info("С основной страницы извлечено {} курсов", parsedFromMain);
            } else {
                logger.warn("Не удалось загрузить основную страницу с категориями");
            }

            // Парсим вторую страницу
            logger.debug("Загрузка второй страницы с категориями");
            String secondPageContent = fetchPageContent("/course/index.php?browse=categories&page=1&categoryid=0");

            if (secondPageContent != null) {
                logger.debug("Вторая страница загружена успешно, начинаем парсинг");
                int beforeSecondParse = courses.size();
                parseCoursesFromPage(secondPageContent, courses);
                int parsedFromSecond = courses.size() - beforeSecondParse;
                logger.info("Со второй страницы извлечено {} курсов", parsedFromSecond);
            } else {
                logger.warn("Не удалось загрузить вторую страницу с категориями");
            }

            long endTime = System.currentTimeMillis();
            logger.info("Парсинг завершен. Всего найдено {} курсов за {} мс",
                    courses.size(), (endTime - startTime));

        } catch (Exception e) {
            logger.error("Критическая ошибка во время парсинга курсов", e);
        }

        return courses;
    }

    String fetchPageContent(String url) {
        logger.debug("Запрос к URL: {}", url);

        try {
            ResponseEntity<String> response = restClient.get()
                    .uri(url)
                    .retrieve()
                    .toEntity(String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                String content = response.getBody();
                int contentLength = content != null ? content.length() : 0;
                logger.debug("Успешный ответ от сервера. Длина контента: {} символов", contentLength);
                return content;
            } else {
                logger.warn("Сервер вернул ошибку HTTP {} для URL: {}", response.getStatusCode(), url);
                return null;
            }

        } catch (Exception e) {
            logger.error("Ошибка при загрузке страницы {}: {}", url, e.getMessage(), e);
            return null;
        }
    }

    private void parseCoursesFromPage(String htmlContent, TreeSet<CourseAiDTO> courses) {
        logger.trace("Начало парсинга курсов из HTML контента");

        int initialCourseCount = courses.size();

        // Парсим курсы из выпадающего списка категорий
        int dropdownCoursesFound = parseCoursesFromDropdown(htmlContent, courses);

        // Парсим курсы из структуры категорий на главной странице
        int structureCoursesFound = parseCoursesFromCategoryStructure(htmlContent, courses);

        // Парсим курсы из блока "Категории курсов"
        int frontpageCoursesFound = parseCoursesFromFrontpageCategories(htmlContent, courses);

        int totalNewCourses = courses.size() - initialCourseCount;
        logger.info("Всего извлечено новых курсов с этой страницы: {} ({} из выпадающего списка, {} из структуры, {} с главной страницы)",
                totalNewCourses, dropdownCoursesFound, structureCoursesFound, frontpageCoursesFound);
    }

    private int parseCoursesFromDropdown(String htmlContent, TreeSet<CourseAiDTO> courses) {
        logger.trace("Парсинг курсов из выпадающего списка");

        int dropdownCoursesFound = 0;

        // Регулярное выражение для поиска ссылок на курсы в выпадающем списке
        Pattern coursePattern = Pattern.compile(
                "<option value=\"(/course/index\\.php\\?categoryid=\\d+)\">([^<]+)</option>"
        );

        Matcher matcher = coursePattern.matcher(htmlContent);

        while (matcher.find()) {
            String courseUrl = matcher.group(1);
            String fullCourseName = matcher.group(2).trim();

            logger.trace("Найден курс в выпадающем списке: {} -> {}", fullCourseName, courseUrl);

            // Извлекаем категорию и название курса
            String[] parts = fullCourseName.split(" / ");
            String category = "";
            String courseName = fullCourseName;

            if (parts.length > 1) {
                category = parts[parts.length - 2]; // Предпоследний элемент как категория
                courseName = parts[parts.length - 1]; // Последний элемент как название курса
                logger.trace("Разделено на категорию '{}' и курс '{}'", category, courseName);
            } else if (parts.length == 1) {
                logger.trace("Не удалось разделить на категорию, используем полное имя как название курса");
            }

            // Формируем полный URL
            String fullUrl = "https://elearn.urfu.ru" + courseUrl;

            // Создаем DTO и добавляем в множество
            CourseAiDTO course = new CourseAiDTO(courseName, category, fullUrl);
            if (courses.add(course)) {
                dropdownCoursesFound++;
                logger.debug("Добавлен новый курс из выпадающего списка: {}", courseName);
            } else {
                logger.trace("Курс '{}' уже существует в коллекции", courseName);
            }
        }

        logger.info("Из выпадающего списка извлечено {} уникальных курсов", dropdownCoursesFound);
        return dropdownCoursesFound;
    }

    private int parseCoursesFromCategoryStructure(String htmlContent, TreeSet<CourseAiDTO> courses) {
        logger.trace("Парсинг курсов из структуры категорий");

        int structureCoursesFound = 0;

        // Регулярное выражение для поиска курсов в структуре категорий
        Pattern categoryPattern = Pattern.compile(
                "<div class=\"category[^\"]*\"[^>]*data-categoryid=\"(\\d+)\"[^>]*>\\s*<div class=\"info\">\\s*<h3 class=\"categoryname[^\"]*\">\\s*<a href=\"([^\"]+)\">([^<]+)</a>"
        );

        Matcher matcher = categoryPattern.matcher(htmlContent);

        while (matcher.find()) {
            String categoryId = matcher.group(1);
            String categoryUrl = matcher.group(2);
            String categoryName = matcher.group(3).trim();

            logger.trace("Найдена категория: {} (ID: {})", categoryName, categoryId);

            // Если это конечная категория (содержит номер), считаем её курсом
            if (isCourseCategory(categoryName)) {
                String fullUrl = categoryUrl.startsWith("http") ? categoryUrl : "https://elearn.urfu.ru" + categoryUrl;

                // Разделяем путь категории для извлечения названия курса и категории
                String[] pathParts = categoryName.split(" / ");
                String courseName = categoryName;
                String parentCategory = "";

                if (pathParts.length > 1) {
                    courseName = pathParts[pathParts.length - 1];
                    parentCategory = pathParts.length > 2 ? pathParts[pathParts.length - 2] : pathParts[0];
                    logger.trace("Разделен путь категории: курс='{}', родительская категория='{}'",
                            courseName, parentCategory);
                }

                CourseAiDTO course = new CourseAiDTO(courseName, parentCategory, fullUrl);
                if (courses.add(course)) {
                    structureCoursesFound++;
                    logger.debug("Добавлен курс из структуры категорий: {}", courseName);
                } else {
                    logger.trace("Курс из структуры '{}' уже существует в коллекции", courseName);
                }
            } else {
                logger.trace("Категория '{}' не идентифицирована как курс, пропускаем", categoryName);
            }
        }

        logger.info("Из структуры категорий извлечено {} уникальных курсов", structureCoursesFound);
        return structureCoursesFound;
    }

    private int parseCoursesFromFrontpageCategories(String htmlContent, TreeSet<CourseAiDTO> courses) {
        logger.trace("Парсинг курсов с главной страницы (блок 'Категории курсов')");

        int frontpageCoursesFound = 0;

        // Регулярное выражение для поиска категорий курсов на главной странице
        Pattern frontpagePattern = Pattern.compile(
                "<div class=\"category[^\"]*\"[^>]*data-categoryid=\"(\\d+)\"[^>]*data-depth=\"1\"[^>]*>\\s*<div class=\"info\">\\s*<h3 class=\"categoryname[^\"]*\">\\s*<a href=\"([^\"]+)\">([^<]+)</a>\\s*<span[^>]*>\\((\\d+)\\)</span>"
        );

        Matcher matcher = frontpagePattern.matcher(htmlContent);

        while (matcher.find()) {
            String categoryId = matcher.group(1);
            String categoryUrl = matcher.group(2);
            String categoryName = matcher.group(3).trim();
            String courseCount = matcher.group(4);

            logger.debug("Найдена категория на главной странице: {} (ID: {}, курсов: {})",
                    categoryName, categoryId, courseCount);

            String fullUrl = categoryUrl.startsWith("http") ? categoryUrl : "https://elearn.urfu.ru" + categoryUrl;

            // Для главных категорий используем их как курсы верхнего уровня
            CourseAiDTO course = new CourseAiDTO(categoryName, "Основные категории", fullUrl);
            if (courses.add(course)) {
                frontpageCoursesFound++;
                logger.debug("Добавлена основная категория как курс: {}", categoryName);
            }
        }

        logger.info("С главной страницы извлечено {} основных категорий", frontpageCoursesFound);
        return frontpageCoursesFound;
    }

    private boolean isCourseCategory(String categoryName) {
        // Проверяем, содержит ли название категории номер курса или выглядит как курс
        boolean isCourse = categoryName.matches(".*\\d+.*") ||
                categoryName.matches(".*[A-Za-z]+\\s+\\d+.*") ||
                categoryName.length() < 100; // Эвристика: короткие названия скорее всего курсы

        if (logger.isTraceEnabled()) {
            logger.trace("Проверка категории '{}': isCourse={}", categoryName, isCourse);
        }

        return isCourse;
    }

    // Метод для получения Map<K, V> как требовалось в задании
    public Map<String, String> parseCoursesAsMap() {
        logger.info("Начало парсинга курсов в формате Map");

        TreeSet<CourseAiDTO> courses = parseAllCourses();
        Map<String, String> courseMap = new TreeMap<>();

        for (CourseAiDTO course : courses) {
            courseMap.put(course.name(), course.url());
        }

        logger.info("Создана Map с {} записями курсов", courseMap.size());
        return courseMap;
    }
}