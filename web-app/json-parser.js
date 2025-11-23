/**
 * Скрипт для автоматического парсинга JSON от backend и заполнения HTML полей
 * Работает на всех страницах приложения
 */

const JSONParser = {
    /**
     * Основная функция для парсинга JSON и заполнения HTML полей
     * 
     * @param {Object|Array} jsonData - JSON данные от backend
     * @param {string} pageType - Тип страницы ('profile', 'chats', 'messages', 'courses', 'auto')
     */
    parseAndFill(jsonData, pageType = 'auto') {
        console.log('[JSON Parser] Начало парсинга JSON данных:', jsonData);
        console.log('[JSON Parser] Тип страницы:', pageType);

        if (!jsonData) {
            console.warn('[JSON Parser] Данные пусты');
            return;
        }

        // Автоматическое определение типа данных
        if (pageType === 'auto') {
            pageType = this.detectDataType(jsonData);
            console.log('[JSON Parser] Определен тип данных:', pageType);
        }

        // Парсинг в зависимости от типа
        switch (pageType) {
            case 'profile':
            case 'user':
                this.fillUserProfile(jsonData);
                break;
            case 'chats':
            case 'chatList':
                this.fillChatsList(jsonData);
                break;
            case 'messages':
            case 'chatMessages':
                this.fillChatMessages(jsonData);
                break;
            case 'courses':
                this.fillCourses(jsonData);
                break;
            default:
                this.fillGenericData(jsonData);
        }

        console.log('[JSON Parser] Парсинг завершен');
    },

    /**
     * Автоматическое определение типа данных
     */
    detectDataType(jsonData) {
        if (Array.isArray(jsonData)) {
            if (jsonData.length > 0) {
                const firstItem = jsonData[0];
                if (firstItem.title || firstItem.chatId || firstItem.id) {
                    return 'chats';
                } else if (firstItem.text || firstItem.content || firstItem.message) {
                    return 'messages';
                } else if (firstItem.courseName || firstItem.course) {
                    return 'courses';
                }
            }
        } else if (typeof jsonData === 'object') {
            if (jsonData.name || jsonData.email || jsonData.userId) {
                return 'profile';
            } else if (jsonData.messages && Array.isArray(jsonData.messages)) {
                return 'messages';
            } else if (jsonData.chats && Array.isArray(jsonData.chats)) {
                return 'chats';
            }
        }
        return 'generic';
    },

    /**
     * Заполнить профиль пользователя из JSON
     * Поддерживает структуру данных студента из /auth
     */
    fillUserProfile(profileData) {
        console.log('[JSON Parser] Заполнение профиля пользователя:', profileData);

        // Если это структура из /auth с полем student
        if (profileData.student) {
            return this.fillStudentProfile(profileData.student, profileData.token);
        }

        // Имя пользователя
        if (profileData.name || profileData.username || profileData.fullName || profileData.studentFio) {
            const name = profileData.name || profileData.username || profileData.fullName || profileData.studentFio || 'Студент';
            this.fillElement('.student-name', name);
            this.fillElement('#userName', name);
            console.log('[JSON Parser] Имя пользователя обновлено:', name);
        }

        // Email пользователя (получаем из токена или сохраняем отдельно)
        const email = profileData.email || profileData.emailAddress;
        if (email) {
            this.fillElement('.student-email', email);
            this.fillElement('#userEmail', email);
            console.log('[JSON Parser] Email пользователя обновлен:', email);
        }

        // ID пользователя
        if (profileData.id || profileData.userId || profileData.studentId) {
            const id = profileData.id || profileData.userId || profileData.studentId;
            this.fillElement('#userId', id);
            this.fillElement('[data-user-id]', id);
            console.log('[JSON Parser] ID пользователя обновлен:', id);
        }

        // Группа (академическая группа)
        if (profileData.group || profileData.groupName || profileData.academicGroup) {
            const group = profileData.group || profileData.groupName || profileData.academicGroup;
            this.fillElement('[data-user-group]', group);
            console.log('[JSON Parser] Группа обновлена:', group);
        }

        // Университет
        if (profileData.university || profileData.universityName) {
            const university = profileData.university || profileData.universityName;
            this.fillElement('[data-user-university]', university);
            console.log('[JSON Parser] Университет обновлен:', university);
        }

        // Сохраняем в localStorage
        localStorage.setItem('userProfile', JSON.stringify(profileData));
        console.log('[JSON Parser] Профиль сохранен в localStorage');
    },

    /**
     * Заполнить профиль студента из структуры данных /auth
     */
    fillStudentProfile(studentData, token = null) {
        console.log('[JSON Parser] Заполнение профиля студента:', studentData);

        // Имя студента (ФИО)
        if (studentData.studentFio) {
            this.fillElement('.student-name', studentData.studentFio);
            this.fillElement('#userName', studentData.studentFio);
            console.log('[JSON Parser] ФИО студента обновлено:', studentData.studentFio);
        } else {
            // Если ФИО нет, используем email из токена
            const email = this.getEmailFromToken(token);
            if (email) {
                this.fillElement('.student-name', email.split('@')[0]);
            }
        }

        // Email (получаем из токена, если есть)
        const email = this.getEmailFromToken(token);
        if (email) {
            this.fillElement('.student-email', email);
            this.fillElement('#userEmail', email);
            console.log('[JSON Parser] Email студента обновлен:', email);
        }

        // Академическая группа
        if (studentData.academicGroup) {
            this.fillElement('[data-user-group]', studentData.academicGroup);
            console.log('[JSON Parser] Академическая группа обновлена:', studentData.academicGroup);
        }

        // Статус обучения
        if (studentData.educationStatus) {
            this.fillElement('[data-education-status]', studentData.educationStatus);
            console.log('[JSON Parser] Статус обучения обновлен:', studentData.educationStatus);
        }

        // Часовой пояс
        if (studentData.timeZone) {
            this.fillElement('[data-timezone]', studentData.timeZone);
            console.log('[JSON Parser] Часовой пояс обновлен:', studentData.timeZone);
        }

        // Сохраняем данные студента в localStorage
        localStorage.setItem('studentData', JSON.stringify(studentData));
        if (studentData.courseDtoList && Array.isArray(studentData.courseDtoList)) {
            localStorage.setItem('coursesData', JSON.stringify(studentData.courseDtoList));
            console.log('[JSON Parser] Курсы сохранены:', studentData.courseDtoList.length);
        }
        
        console.log('[JSON Parser] Профиль студента сохранен в localStorage');
    },

    /**
     * Получить email из JWT токена
     */
    getEmailFromToken(token) {
        if (!token) {
            // Пытаемся получить из localStorage
            const savedToken = localStorage.getItem('authToken');
            if (savedToken) {
                token = savedToken;
            } else {
                return null;
            }
        }

        try {
            // JWT токен состоит из трех частей, разделенных точками
            const parts = token.split('.');
            if (parts.length !== 3) {
                return null;
            }

            // Декодируем payload (вторая часть)
            const payload = JSON.parse(atob(parts[1]));
            return payload.sub || null; // sub обычно содержит email/username
        } catch (e) {
            console.error('[JSON Parser] Ошибка декодирования токена:', e);
            return null;
        }
    },

    /**
     * Заполнить список чатов из JSON
     */
    fillChatsList(chatsData) {
        console.log('[JSON Parser] Заполнение списка чатов:', chatsData);

        if (!Array.isArray(chatsData)) {
            console.error('[JSON Parser] Данные чатов должны быть массивом');
            return;
        }

        // Найти контейнер для чатов
        const container = document.querySelector('#chatsList, .chats-list, [data-chats-container]') 
                       || document.querySelector('.courses-grid');
        
        if (!container) {
            console.warn('[JSON Parser] Контейнер для чатов не найден');
            return;
        }

        // Очистить контейнер
        container.innerHTML = '';

        if (chatsData.length === 0) {
            container.innerHTML = '<div class="no-data">Нет чатов</div>';
            console.log('[JSON Parser] Список чатов пуст');
            return;
        }

        // Создать элементы для каждого чата
        chatsData.forEach((chat, index) => {
            const chatElement = this.createChatCard(chat, index);
            container.appendChild(chatElement);
        });

        console.log(`[JSON Parser] Добавлено ${chatsData.length} чатов`);
    },

    /**
     * Создать карточку чата
     */
    createChatCard(chat, index) {
        const card = document.createElement('div');
        card.className = 'course-card chat-card';
        card.dataset.chatId = chat.id || chat.chatId;

        const title = chat.title || chat.name || `Чат ${index + 1}`;
        const lastMessage = chat.lastMessage || chat.preview || chat.message || 'Нет сообщений';
        const lastDate = this.formatDate(chat.lastMessageDate || chat.updatedAt || chat.date);

        card.innerHTML = `
            <div class="course-type">Чат</div>
            <div class="course-name">${this.escapeHtml(title)}</div>
            <div style="margin-top: 10px; font-size: 14px; color: #999;">${this.escapeHtml(lastMessage)}</div>
            <div style="margin-top: 5px; font-size: 12px; color: #999;">${lastDate}</div>
        `;

        // Добавить обработчик клика
        card.addEventListener('click', () => {
            console.log('[JSON Parser] Открыт чат:', chat.id || chat.chatId);
            const chatId = chat.id || chat.chatId;
            if (chatId) {
                window.location.href = `chat.html?chatId=${chatId}`;
            }
        });

        return card;
    },

    /**
     * Заполнить сообщения чата из JSON
     */
    fillChatMessages(messagesData) {
        console.log('[JSON Parser] Заполнение сообщений чата:', messagesData);

        if (!Array.isArray(messagesData)) {
            console.error('[JSON Parser] Данные сообщений должны быть массивом');
            return;
        }

        const container = document.querySelector('#chatMessages, .chat-messages, [data-messages-container]');
        
        if (!container) {
            console.warn('[JSON Parser] Контейнер для сообщений не найден');
            return;
        }

        // Очистить контейнер
        container.innerHTML = '';

        if (messagesData.length === 0) {
            console.log('[JSON Parser] Нет сообщений');
            return;
        }

        // Создать элементы для каждого сообщения
        messagesData.forEach((message) => {
            const messageElement = this.createMessageElement(message);
            container.appendChild(messageElement);
        });

        // Прокрутить вниз
        container.scrollTop = container.scrollHeight;

        console.log(`[JSON Parser] Добавлено ${messagesData.length} сообщений`);
    },

    /**
     * Создать элемент сообщения
     */
    createMessageElement(message) {
        const messageDiv = document.createElement('div');
        messageDiv.className = `message ${message.isUser ? 'user-message' : 'bot-message'}`;

        const messageContent = document.createElement('div');
        messageContent.className = 'message-content';

        const messageText = document.createElement('div');
        messageText.className = 'message-text';
        messageText.textContent = message.text || message.content || message.message || '';

        messageContent.appendChild(messageText);

        // Добавить кнопку копирования для сообщений бота
        if (!message.isUser) {
            const copyButton = document.createElement('button');
            copyButton.className = 'copy-button';
            copyButton.title = 'Скопировать';
            copyButton.innerHTML = `
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <rect x="9" y="9" width="13" height="13" rx="2" ry="2"></rect>
                    <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"></path>
                </svg>
            `;
            copyButton.addEventListener('click', () => {
                navigator.clipboard.writeText(message.text || message.content || '');
                console.log('[JSON Parser] Сообщение скопировано');
            });
            messageContent.appendChild(copyButton);
        }

        messageDiv.appendChild(messageContent);
        return messageDiv;
    },

    /**
     * Заполнить курсы из JSON
     * Поддерживает структуру courseDtoList из /auth
     */
    fillCourses(coursesData) {
        console.log('[JSON Parser] Заполнение курсов:', coursesData);

        // Если это структура из /auth с полем student
        if (coursesData.student && coursesData.student.courseDtoList) {
            coursesData = coursesData.student.courseDtoList;
        } else if (!Array.isArray(coursesData)) {
            console.error('[JSON Parser] Данные курсов должны быть массивом');
            return;
        }

        const container = document.querySelector('.courses-grid, #coursesGrid, #coursesList, [data-courses-container]');
        
        if (!container) {
            console.warn('[JSON Parser] Контейнер для курсов не найден');
            return;
        }

        // Очистить контейнер
        container.innerHTML = '';

        if (coursesData.length === 0) {
            container.innerHTML = '<div class="no-data" style="text-align: center; padding: 40px; color: #999;">Нет доступных курсов</div>';
            console.log('[JSON Parser] Список курсов пуст');
            return;
        }

        coursesData.forEach((course) => {
            const courseElement = this.createCourseCard(course);
            container.appendChild(courseElement);
        });

        console.log(`[JSON Parser] Добавлено ${coursesData.length} курсов`);
    },

    /**
     * Создать карточку курса
     * Поддерживает структуру courseDto из /auth
     */
    createCourseCard(course) {
        const card = document.createElement('div');
        card.className = 'course-card';
        
        // ID курса из URL или имени
        const courseId = course.id || course.courseId || (course.url ? course.url.match(/id=(\d+)/)?.[1] : null);
        if (courseId) {
            card.dataset.courseId = courseId;
        }

        const courseName = course.name || course.courseName || course.title || 'Курс';
        const courseType = course.type || course.courseCategory || 'Курс';
        const courseUrl = course.url || null;

        card.innerHTML = `
            <div class="course-type">${this.escapeHtml(courseType)}</div>
            <div class="course-name">${this.escapeHtml(courseName)}</div>
            ${course.courseCategory ? `<div style="margin-top: 10px; font-size: 14px; color: #666;">${this.escapeHtml(course.courseCategory)}</div>` : ''}
        `;

        // Если есть URL, делаем карточку кликабельной
        if (courseUrl) {
            card.style.cursor = 'pointer';
            card.addEventListener('click', () => {
                console.log('[JSON Parser] Открытие курса:', courseUrl);
                window.open(courseUrl, '_blank');
            });
        }

        return card;
    },

    /**
     * Заполнить общие данные из JSON (рекурсивно заполняет все поля)
     */
    fillGenericData(jsonData, prefix = '') {
        console.log('[JSON Parser] Заполнение общих данных:', jsonData);

        if (typeof jsonData !== 'object' || jsonData === null) {
            return;
        }

        for (const key in jsonData) {
            if (jsonData.hasOwnProperty(key)) {
                const value = jsonData[key];
                const selector = prefix ? `${prefix}-${key}` : key;

                // Попробовать найти элемент по разным селекторам
                this.fillElement(`#${selector}`, value);
                this.fillElement(`[data-${selector}]`, value);
                this.fillElement(`.${selector}`, value);

                // Если значение - объект, рекурсивно обработать
                if (typeof value === 'object' && value !== null && !Array.isArray(value)) {
                    this.fillGenericData(value, selector);
                }
            }
        }
    },

    /**
     * Заполнить элемент по селектору
     */
    fillElement(selector, value) {
        const element = document.querySelector(selector);
        if (!element) {
            return false;
        }

        // Если значение - объект или массив, преобразовать в строку
        if (typeof value === 'object') {
            if (Array.isArray(value)) {
                element.textContent = value.join(', ');
            } else {
                element.textContent = JSON.stringify(value, null, 2);
            }
        } else {
            element.textContent = value || '';
        }

        console.log(`[JSON Parser] Заполнен элемент ${selector}:`, value);
        return true;
    },

    /**
     * Форматировать дату
     */
    formatDate(date) {
        if (!date) return '';

        const dateObj = typeof date === 'string' ? new Date(date) : date;
        const now = new Date();
        const diff = now - dateObj;
        const minutes = Math.floor(diff / 60000);
        const hours = Math.floor(diff / 3600000);
        const days = Math.floor(diff / 86400000);

        if (minutes < 1) return 'Только что';
        if (minutes < 60) return `${minutes} мин. назад`;
        if (hours < 24) return `${hours} ч. назад`;
        if (days === 1) return 'Вчера';
        if (days < 7) return `${days} дн. назад`;
        return dateObj.toLocaleDateString('ru-RU');
    },

    /**
     * Экранировать HTML
     */
    escapeHtml(text) {
        if (!text) return '';
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    },

    /**
     * Загрузить и заполнить профиль пользователя с backend или из localStorage
     */
    async loadAndFillProfile() {
        console.log('[JSON Parser] Загрузка профиля пользователя...');
        
        try {
            // Сначала пытаемся загрузить из localStorage (данные из /auth)
            const studentDataStr = localStorage.getItem('studentData');
            if (studentDataStr) {
                const studentData = JSON.parse(studentDataStr);
                const token = localStorage.getItem('authToken');
                console.log('[JSON Parser] Загрузка профиля из localStorage');
                this.fillStudentProfile(studentData, token);
                return;
            }

            // Если нет в localStorage, пытаемся загрузить с backend
            if (window.API && window.API.getUserProfile) {
                console.log('[JSON Parser] Загрузка профиля с backend...');
                const profile = await window.API.getUserProfile();
                this.fillUserProfile(profile);
                console.log('[JSON Parser] Профиль успешно загружен с backend');
            } else {
                console.warn('[JSON Parser] API.getUserProfile не доступен');
            }
        } catch (error) {
            console.error('[JSON Parser] Ошибка при загрузке профиля:', error);
        }
    },

    /**
     * Загрузить и заполнить курсы из localStorage или backend
     */
    async loadAndFillCourses() {
        console.log('[JSON Parser] Загрузка курсов...');
        
        try {
            // Сначала пытаемся загрузить из localStorage (данные из /auth)
            const coursesDataStr = localStorage.getItem('coursesData');
            if (coursesDataStr) {
                const coursesData = JSON.parse(coursesDataStr);
                console.log('[JSON Parser] Загрузка курсов из localStorage:', coursesData.length);
                this.fillCourses(coursesData);
                return;
            }

            // Если нет в localStorage, пытаемся загрузить из studentData
            const studentDataStr = localStorage.getItem('studentData');
            if (studentDataStr) {
                const studentData = JSON.parse(studentDataStr);
                if (studentData.courseDtoList && Array.isArray(studentData.courseDtoList)) {
                    console.log('[JSON Parser] Загрузка курсов из studentData:', studentData.courseDtoList.length);
                    this.fillCourses(studentData.courseDtoList);
                    return;
                }
            }

            // Если нет данных в localStorage, показываем сообщение
            const container = document.querySelector('.courses-grid, #coursesGrid, #coursesList, [data-courses-container]');
            if (container) {
                container.innerHTML = '<div class="no-data" style="text-align: center; padding: 40px; color: #999;">Нет доступных курсов</div>';
            }
            console.log('[JSON Parser] Курсы не найдены в localStorage');
        } catch (error) {
            console.error('[JSON Parser] Ошибка при загрузке курсов:', error);
        }
    },

    /**
     * Загрузить и заполнить список чатов с backend
     */
    async loadAndFillChats() {
        console.log('[JSON Parser] Загрузка списка чатов с backend...');
        
        try {
            if (!window.API || !window.API.getAllChats) {
                console.error('[JSON Parser] API.getAllChats не доступен');
                return;
            }

            const chats = await window.API.getAllChats();
            console.log('[JSON Parser] Чаты получены:', chats);
            this.fillChatsList(chats);
            console.log('[JSON Parser] Список чатов заполнен');
        } catch (error) {
            console.error('[JSON Parser] Ошибка при загрузке чатов:', error);
        }
    },

    /**
     * Загрузить и заполнить сообщения чата с backend
     */
    async loadAndFillMessages(chatId) {
        console.log('[JSON Parser] Загрузка сообщений чата с backend...', chatId);
        
        try {
            if (!window.API || !window.API.getChatMessages) {
                console.error('[JSON Parser] API.getChatMessages не доступен');
                return;
            }

            const messages = await window.API.getChatMessages(chatId);
            console.log('[JSON Parser] Сообщения получены:', messages);
            this.fillChatMessages(messages);
            console.log('[JSON Parser] Сообщения заполнены');
        } catch (error) {
            console.error('[JSON Parser] Ошибка при загрузке сообщений:', error);
        }
    }
};

// Экспорт для использования в других файлах
if (typeof window !== 'undefined') {
    window.JSONParser = JSONParser;
    console.log('[JSON Parser] JSON Parser загружен и доступен через window.JSONParser');
}

// Автоматическая загрузка данных при загрузке страницы
document.addEventListener('DOMContentLoaded', function() {
    console.log('[JSON Parser] DOM загружен, начало автоматической загрузки данных...');
    
    // Проверяем авторизацию (токен в localStorage)
    const token = localStorage.getItem('authToken');
    const isAuthenticated = token !== null;
    
    if (isAuthenticated || (window.API && window.API.isAuthenticated())) {
        console.log('[JSON Parser] Пользователь авторизован, загружаем данные...');
        
        // Загружаем профиль на всех страницах
        JSONParser.loadAndFillProfile();
        
        // Загружаем курсы на страницах с курсами
        const coursesContainer = document.querySelector('.courses-grid, #coursesGrid');
        if (coursesContainer && (window.location.pathname.includes('index') || 
                                 window.location.pathname.includes('courses') ||
                                 window.location.pathname.includes('progress'))) {
            JSONParser.loadAndFillCourses();
        }
        
        // Загружаем чаты, если есть контейнер для них
        const chatsContainer = document.querySelector('#chatsList, .chats-list, [data-chats-container]');
        if (chatsContainer || window.location.pathname.includes('chat.html')) {
            JSONParser.loadAndFillChats();
        }
        
        // Загружаем сообщения, если есть chatId в URL
        const urlParams = new URLSearchParams(window.location.search);
        const chatId = urlParams.get('chatId');
        if (chatId) {
            JSONParser.loadAndFillMessages(chatId);
        }
    } else {
        console.log('[JSON Parser] Пользователь не авторизован');
    }
});

