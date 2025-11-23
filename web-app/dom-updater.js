/**
 * Утилиты для парсинга JSON и обновления HTML элементов
 * Используется для интеграции backend данных с frontend
 */

const DOMUpdater = {
    /**
     * Обновить элемент по ID с данными из объекта
     * 
     * @param {string} elementId - ID элемента
     * @param {any} data - Данные для вставки
     * @param {string} property - Свойство для вставки (если не указано, используется data напрямую)
     */
    updateElementById(elementId, data, property = null) {
        const element = document.getElementById(elementId);
        if (!element) {
            console.warn(`[DOM Updater] Элемент с ID "${elementId}" не найден`);
            return;
        }

        let value = property ? (data[property] || '') : data;
        element.textContent = value || '';
        console.log(`[DOM Updater] Обновлен элемент #${elementId}:`, value);
    },

    /**
     * Обновить элемент по селектору
     * 
     * @param {string} selector - CSS селектор
     * @param {any} data - Данные для вставки
     * @param {string} property - Свойство объекта
     */
    updateElement(selector, data, property = null) {
        const element = document.querySelector(selector);
        if (!element) {
            console.warn(`[DOM Updater] Элемент "${selector}" не найден`);
            return;
        }

        let value = property ? (data[property] || '') : data;
        element.textContent = value || '';
        console.log(`[DOM Updater] Обновлен элемент ${selector}:`, value);
    },

    /**
     * Обновить innerHTML элемента
     * 
     * @param {string} elementId - ID элемента
     * @param {string} html - HTML для вставки
     */
    updateInnerHTML(elementId, html) {
        const element = document.getElementById(elementId);
        if (!element) {
            console.warn(`[DOM Updater] Элемент с ID "${elementId}" не найден`);
            return;
        }

        element.innerHTML = html || '';
        console.log(`[DOM Updater] Обновлен innerHTML элемента #${elementId}`);
    },

    /**
     * Обновить атрибут элемента
     * 
     * @param {string} elementId - ID элемента
     * @param {string} attribute - Название атрибута
     * @param {string} value - Значение атрибута
     */
    updateAttribute(elementId, attribute, value) {
        const element = document.getElementById(elementId);
        if (!element) {
            console.warn(`[DOM Updater] Элемент с ID "${elementId}" не найден`);
            return;
        }

        element.setAttribute(attribute, value);
        console.log(`[DOM Updater] Обновлен атрибут ${attribute} элемента #${elementId}:`, value);
    },

    /**
     * Обновить профиль пользователя из данных backend
     * 
     * @param {Object} profileData - Данные профиля с backend
     */
    updateUserProfile(profileData) {
        console.log('[DOM Updater] Обновление профиля пользователя:', profileData);

        // Имя пользователя
        if (profileData.name) {
            this.updateElementById('userName', profileData, 'name');
            this.updateElement('.student-name', profileData, 'name');
            this.updateElement('[data-user-name]', profileData, 'name');
        }

        // Email пользователя
        if (profileData.email) {
            this.updateElementById('userEmail', profileData, 'email');
            this.updateElement('.student-email', profileData, 'email');
            this.updateElement('[data-user-email]', profileData, 'email');
        }

        // ID пользователя
        if (profileData.id) {
            this.updateElementById('userId', profileData, 'id');
            this.updateElement('[data-user-id]', profileData, 'id');
        }

        // Дополнительные поля профиля
        if (profileData.group) {
            this.updateElement('[data-user-group]', profileData, 'group');
        }

        if (profileData.studentId) {
            this.updateElement('[data-user-student-id]', profileData, 'studentId');
        }

        if (profileData.university) {
            this.updateElement('[data-user-university]', profileData, 'university');
        }

        // Сохраняем данные профиля в localStorage
        localStorage.setItem('userProfile', JSON.stringify(profileData));
        console.log('[DOM Updater] Профиль сохранен в localStorage');
    },

    /**
     * Обновить список чатов из данных backend
     * 
     * @param {Array} chatsData - Массив чатов с backend
     * @param {string} containerId - ID контейнера для чатов
     */
    updateChatsList(chatsData, containerId = 'chatsList') {
        console.log('[DOM Updater] Обновление списка чатов:', chatsData);

        const container = document.getElementById(containerId);
        if (!container) {
            console.warn(`[DOM Updater] Контейнер #${containerId} не найден`);
            return;
        }

        if (!Array.isArray(chatsData)) {
            console.error('[DOM Updater] Данные чатов должны быть массивом');
            return;
        }

        // Очищаем контейнер
        container.innerHTML = '';

        if (chatsData.length === 0) {
            container.innerHTML = '<div class="no-chats">Нет сохраненных чатов</div>';
            console.log('[DOM Updater] Список чатов пуст');
            return;
        }

        // Создаем элементы для каждого чата
        chatsData.forEach((chat, index) => {
            const chatElement = this.createChatElement(chat, index);
            container.appendChild(chatElement);
        });

        console.log(`[DOM Updater] Добавлено ${chatsData.length} чатов в список`);
    },

    /**
     * Создать HTML элемент для чата
     * 
     * @param {Object} chat - Данные чата
     * @param {number} index - Индекс в списке
     * @returns {HTMLElement} Созданный элемент
     */
    createChatElement(chat, index) {
        const chatDiv = document.createElement('div');
        chatDiv.className = 'chat-item';
        chatDiv.dataset.chatId = chat.id || chat.chatId;

        const lastMessage = chat.lastMessage || chat.preview || 'Нет сообщений';
        const lastMessageDate = this.formatDate(chat.lastMessageDate || chat.updatedAt || new Date());

        chatDiv.innerHTML = `
            <div class="chat-icon">
                <img src="gigachat-sign-logo.png" alt="Chat" />
            </div>
            <div class="chat-info">
                <div class="chat-title">${this.escapeHtml(chat.title || `Чат ${index + 1}`)}</div>
                <div class="chat-preview">
                    <span class="chat-message">${this.escapeHtml(lastMessage)}</span>
                    <span class="chat-separator">•</span>
                    <span class="chat-date">${lastMessageDate}</span>
                </div>
            </div>
        `;

        // Добавляем обработчик клика
        chatDiv.addEventListener('click', () => {
            console.log(`[DOM Updater] Открыт чат: ${chat.id || chat.chatId}`);
            if (window.openChat) {
                window.openChat(chat.id || chat.chatId);
            }
        });

        return chatDiv;
    },

    /**
     * Обновить сообщения чата
     * 
     * @param {Array} messagesData - Массив сообщений с backend
     * @param {string} containerId - ID контейнера для сообщений
     */
    updateChatMessages(messagesData, containerId = 'chatMessages') {
        console.log('[DOM Updater] Обновление сообщений чата:', messagesData);

        const container = document.getElementById(containerId);
        if (!container) {
            console.warn(`[DOM Updater] Контейнер #${containerId} не найден`);
            return;
        }

        if (!Array.isArray(messagesData)) {
            console.error('[DOM Updater] Данные сообщений должны быть массивом');
            return;
        }

        // Очищаем контейнер
        container.innerHTML = '';

        if (messagesData.length === 0) {
            console.log('[DOM Updater] Нет сообщений в чате');
            return;
        }

        // Создаем элементы для каждого сообщения
        messagesData.forEach((message) => {
            const messageElement = this.createMessageElement(message);
            container.appendChild(messageElement);
        });

        // Прокручиваем вниз к последнему сообщению
        container.scrollTop = container.scrollHeight;

        console.log(`[DOM Updater] Добавлено ${messagesData.length} сообщений`);
    },

    /**
     * Создать HTML элемент для сообщения
     * 
     * @param {Object} message - Данные сообщения
     * @returns {HTMLElement} Созданный элемент
     */
    createMessageElement(message) {
        const messageDiv = document.createElement('div');
        messageDiv.className = `message ${message.isUser ? 'user-message' : 'bot-message'}`;

        const messageContent = document.createElement('div');
        messageContent.className = 'message-content';

        const messageText = document.createElement('div');
        messageText.className = 'message-text';
        messageText.textContent = message.text || message.content || '';

        messageContent.appendChild(messageText);

        // Добавляем кнопку копирования для сообщений бота
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
                console.log('[DOM Updater] Сообщение скопировано');
            });
            messageContent.appendChild(copyButton);
        }

        messageDiv.appendChild(messageContent);
        return messageDiv;
    },

    /**
     * Форматировать дату для отображения
     * 
     * @param {string|Date} date - Дата для форматирования
     * @returns {string} Отформатированная дата
     */
    formatDate(date) {
        if (!date) return '';

        const dateObj = typeof date === 'string' ? new Date(date) : date;
        const now = new Date();
        const diff = now - dateObj;
        const minutes = Math.floor(diff / 60000);
        const hours = Math.floor(diff / 3600000);
        const days = Math.floor(diff / 86400000);

        if (minutes < 1) {
            return 'Только что';
        } else if (minutes < 60) {
            return `${minutes} мин. назад`;
        } else if (hours < 24) {
            return `${hours} ч. назад`;
        } else if (days === 1) {
            return 'Вчера';
        } else if (days < 7) {
            return `${days} дн. назад`;
        } else {
            return dateObj.toLocaleDateString('ru-RU');
        }
    },

    /**
     * Экранировать HTML для безопасности
     * 
     * @param {string} text - Текст для экранирования
     * @returns {string} Экранированный текст
     */
    escapeHtml(text) {
        if (!text) return '';
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    },

    /**
     * Обновить данные на странице из JSON ответа backend
     * Автоматически определяет тип данных и обновляет соответствующие элементы
     * 
     * @param {Object|Array} data - JSON данные с backend
     * @param {string} dataType - Тип данных ('profile', 'chats', 'messages', 'auto')
     */
    updateFromBackend(data, dataType = 'auto') {
        console.log('[DOM Updater] Обновление данных с backend:', data);
        console.log('[DOM Updater] Тип данных:', dataType);

        if (!data) {
            console.warn('[DOM Updater] Данные пусты');
            return;
        }

        // Автоматическое определение типа
        if (dataType === 'auto') {
            if (data.email || data.name || data.id) {
                dataType = 'profile';
            } else if (Array.isArray(data) && data.length > 0 && (data[0].title || data[0].chatId)) {
                dataType = 'chats';
            } else if (Array.isArray(data) && data.length > 0 && (data[0].text || data[0].content)) {
                dataType = 'messages';
            }
        }

        switch (dataType) {
            case 'profile':
                this.updateUserProfile(data);
                break;
            case 'chats':
                this.updateChatsList(data);
                break;
            case 'messages':
                this.updateChatMessages(data);
                break;
            default:
                console.warn('[DOM Updater] Неизвестный тип данных:', dataType);
        }
    },

    /**
     * Загрузить и обновить профиль пользователя с backend
     */
    async loadAndUpdateProfile() {
        console.log('[DOM Updater] Загрузка профиля пользователя с backend...');
        
        try {
            if (!window.API || !window.API.getUserProfile) {
                console.error('[DOM Updater] API.getUserProfile не доступен');
                return;
            }

            const profile = await window.API.getUserProfile();
            this.updateUserProfile(profile);
            console.log('[DOM Updater] Профиль успешно загружен и обновлен');
        } catch (error) {
            console.error('[DOM Updater] Ошибка при загрузке профиля:', error);
        }
    },

    /**
     * Загрузить и обновить список чатов с backend
     */
    async loadAndUpdateChats() {
        console.log('[DOM Updater] Загрузка списка чатов с backend...');
        
        try {
            if (!window.API || !window.API.getAllChats) {
                console.error('[DOM Updater] API.getAllChats не доступен');
                return;
            }

            const chats = await window.API.getAllChats();
            this.updateChatsList(chats);
            console.log('[DOM Updater] Список чатов успешно загружен и обновлен');
        } catch (error) {
            console.error('[DOM Updater] Ошибка при загрузке чатов:', error);
        }
    }
};

// Экспорт для использования в других файлах
if (typeof window !== 'undefined') {
    window.DOMUpdater = DOMUpdater;
    console.log('[DOM Updater] DOM Updater загружен и доступен через window.DOMUpdater');
}

