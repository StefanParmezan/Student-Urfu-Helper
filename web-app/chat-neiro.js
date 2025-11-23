document.addEventListener('DOMContentLoaded', function() {
    const promptInput = document.getElementById('promptInput');
    const chatInput = document.getElementById('chatInput');
    const sendButton = document.getElementById('sendButton');
    const chatSendButton = document.getElementById('chatSendButton');
    const chatMessages = document.getElementById('chatMessages');
    const chatWelcome = document.getElementById('chatWelcome');
    const chatMessagesContainer = document.getElementById('chatMessagesContainer');
    const chatWrapper = document.getElementById('chatWrapper');

    let hasStartedChat = false;
    let currentChatId = null; // ID текущего чата
    let currentEventSource = null; // Текущее SSE подключение

    function addMessage(text, isUser = false) {
        const messageDiv = document.createElement('div');
        messageDiv.className = `message ${isUser ? 'user-message' : 'bot-message'}`;
        
        const messageContent = document.createElement('div');
        messageContent.className = 'message-content';
        
        const messageTextWrapper = document.createElement('div');
        messageTextWrapper.className = 'message-text-wrapper';
        
        const messageText = document.createElement('div');
        messageText.className = 'message-text';
        messageText.textContent = text;
        
        messageTextWrapper.appendChild(messageText);
        
        // Добавляем кнопку копирования только для сообщений от бота
        if (!isUser) {
            const copyButton = document.createElement('button');
            copyButton.className = 'copy-button';
            copyButton.innerHTML = '<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="9" y="9" width="13" height="13" rx="2" ry="2"></rect><path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"></path></svg>';
            copyButton.title = 'Копировать';
            
            copyButton.addEventListener('click', function(e) {
                e.stopPropagation();
                navigator.clipboard.writeText(text).then(() => {
                    copyButton.innerHTML = '<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"></polyline></svg>';
                    copyButton.style.color = '#4CAF50';
                    setTimeout(() => {
                        copyButton.innerHTML = '<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="9" y="9" width="13" height="13" rx="2" ry="2"></rect><path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"></path></svg>';
                        copyButton.style.color = '';
                    }, 2000);
                });
            });
            
            messageTextWrapper.appendChild(copyButton);
        }
        
        messageContent.appendChild(messageTextWrapper);
        messageDiv.appendChild(messageContent);
        chatMessages.appendChild(messageDiv);
        
        // Прокрутка вниз
        setTimeout(() => {
            chatMessages.scrollTop = chatMessages.scrollHeight;
        }, 100);
    }

    function startChat() {
        if (!hasStartedChat) {
            hasStartedChat = true;
            chatWelcome.style.display = 'none';
            chatMessagesContainer.style.display = 'flex';
            
            // Добавляем приветственное сообщение бота
            setTimeout(() => {
                addMessage('Здравствуйте! Я ваш образовательный ассистент. Чем могу помочь?', false);
            }, 300);
        }
    }

    function sendMessage(inputElement) {
        const text = inputElement.value.trim();
        if (!text) return;

        // Проверяем авторизацию
        if (!window.API || !window.API.isAuthenticated()) {
            console.error('[Chat] Пользователь не авторизован');
            alert('Пожалуйста, войдите в систему для отправки сообщений');
            return;
        }

        // Запускаем чат при первом сообщении
        if (!hasStartedChat) {
            startChat();
        }

        // Добавляем сообщение пользователя
        addMessage(text, true);
        
        // Очищаем поле ввода
        inputElement.value = '';
        adjustTextareaHeight(inputElement);
        
        // Показываем индикатор загрузки
        const loadingMessage = addLoadingMessage();
        
        // Отправляем сообщение на backend через API
        if (currentChatId) {
            // Отправляем в существующий чат
            sendMessageToExistingChat(currentChatId, text, loadingMessage);
        } else {
            // Создаем новый чат
            sendMessageToNewChat(text, loadingMessage);
        }
    }

    /**
     * Отправить сообщение в новый чат через API
     */
    function sendMessageToNewChat(messageText, loadingMessage) {
        console.log('[Chat] Отправка сообщения в новый чат:', messageText);
        
        if (!window.API || !window.API.sendMessageToNewChat) {
            console.error('[Chat] API.sendMessageToNewChat не доступен');
            removeLoadingMessage(loadingMessage);
            addMessage('Ошибка: API недоступен', false);
            return;
        }

        // Закрываем предыдущее SSE подключение, если есть
        if (currentEventSource) {
            currentEventSource.close();
            currentEventSource = null;
        }

        let responseText = '';
        let isComplete = false;

        // Отправляем сообщение и подключаемся к SSE потоку
        window.API.sendMessageToNewChat(
            messageText,
            // onMessage - вызывается при получении каждого сообщения
            function(messageData) {
                console.log('[Chat] Получено сообщение от backend:', messageData);
                
                // Парсим JSON данные от backend
                if (window.JSONParser) {
                    // Если данные в формате объекта, извлекаем текст
                    if (typeof messageData === 'object') {
                        const text = messageData.text || messageData.message || messageData.content || 
                                   JSON.stringify(messageData);
                        
                        // Если это часть ответа (streaming), добавляем к существующему тексту
                        if (!isComplete) {
                            responseText += text;
                            removeLoadingMessage(loadingMessage);
                            
                            // Если сообщение еще не добавлено, создаем новое
                            if (chatMessages.children.length === 0 || 
                                !chatMessages.lastElementChild.classList.contains('bot-message')) {
                                addMessage(responseText, false);
                            } else {
                                // Обновляем последнее сообщение бота
                                const lastMessage = chatMessages.lastElementChild;
                                const messageTextElement = lastMessage.querySelector('.message-text');
                                if (messageTextElement) {
                                    messageTextElement.textContent = responseText;
                                    chatMessages.scrollTop = chatMessages.scrollHeight;
                                }
                            }
                        }
                        
                        // Проверяем, завершено ли сообщение
                        if (messageData.complete || messageData.done || messageData.finished) {
                            isComplete = true;
                        }
                        
                        // Если есть chatId, сохраняем его
                        if (messageData.chatId && !currentChatId) {
                            currentChatId = messageData.chatId;
                            console.log('[Chat] Установлен ID чата:', currentChatId);
                        }
                    } else {
                        // Если данные в формате строки
                        responseText += messageData;
                        removeLoadingMessage(loadingMessage);
                        addMessage(responseText, false);
                    }
                } else {
                    // Если JSON Parser недоступен, используем прямую обработку
                    const text = typeof messageData === 'string' ? messageData : 
                                (messageData.text || messageData.message || JSON.stringify(messageData));
                    responseText += text;
                    removeLoadingMessage(loadingMessage);
                    addMessage(responseText, false);
                }
            },
            // onError - вызывается при ошибке
            function(error) {
                console.error('[Chat] Ошибка при отправке сообщения:', error);
                removeLoadingMessage(loadingMessage);
                addMessage('Ошибка при отправке сообщения. Попробуйте еще раз.', false);
            },
            // onComplete - вызывается при завершении
            function() {
                console.log('[Chat] SSE подключение завершено');
                isComplete = true;
                currentEventSource = null;
            }
        ).then(eventSource => {
            currentEventSource = eventSource;
            console.log('[Chat] SSE подключение установлено');
        }).catch(error => {
            console.error('[Chat] Ошибка при отправке сообщения:', error);
            removeLoadingMessage(loadingMessage);
            addMessage('Ошибка подключения к серверу. Проверьте подключение к интернету.', false);
        });
    }

    /**
     * Отправить сообщение в существующий чат через API
     */
    function sendMessageToExistingChat(chatId, messageText, loadingMessage) {
        console.log('[Chat] Отправка сообщения в существующий чат:', chatId, messageText);
        
        if (!window.API || !window.API.sendMessageToChat) {
            console.error('[Chat] API.sendMessageToChat не доступен');
            removeLoadingMessage(loadingMessage);
            addMessage('Ошибка: API недоступен', false);
            return;
        }

        // Закрываем предыдущее SSE подключение, если есть
        if (currentEventSource) {
            currentEventSource.close();
            currentEventSource = null;
        }

        let responseText = '';
        let isComplete = false;

        // Отправляем сообщение и подключаемся к SSE потоку
        window.API.sendMessageToChat(
            chatId,
            messageText,
            // onMessage
            function(messageData) {
                console.log('[Chat] Получено сообщение от backend:', messageData);
                
                // Парсим JSON данные от backend
                if (window.JSONParser) {
                    if (typeof messageData === 'object') {
                        const text = messageData.text || messageData.message || messageData.content || 
                                   JSON.stringify(messageData);
                        
                        if (!isComplete) {
                            responseText += text;
                            removeLoadingMessage(loadingMessage);
                            
                            const lastMessage = chatMessages.lastElementChild;
                            if (!lastMessage || !lastMessage.classList.contains('bot-message') ||
                                lastMessage.querySelector('.loading-message')) {
                                addMessage(responseText, false);
                            } else {
                                const messageTextElement = lastMessage.querySelector('.message-text');
                                if (messageTextElement) {
                                    messageTextElement.textContent = responseText;
                                    chatMessages.scrollTop = chatMessages.scrollHeight;
                                }
                            }
                        }
                        
                        if (messageData.complete || messageData.done || messageData.finished) {
                            isComplete = true;
                        }
                    } else {
                        responseText += messageData;
                        removeLoadingMessage(loadingMessage);
                        addMessage(responseText, false);
                    }
                } else {
                    const text = typeof messageData === 'string' ? messageData : 
                                (messageData.text || messageData.message || JSON.stringify(messageData));
                    responseText += text;
                    removeLoadingMessage(loadingMessage);
                    addMessage(responseText, false);
                }
            },
            // onError
            function(error) {
                console.error('[Chat] Ошибка при отправке сообщения:', error);
                removeLoadingMessage(loadingMessage);
                addMessage('Ошибка при отправке сообщения. Попробуйте еще раз.', false);
            },
            // onComplete
            function() {
                console.log('[Chat] SSE подключение завершено');
                isComplete = true;
                currentEventSource = null;
            }
        ).then(eventSource => {
            currentEventSource = eventSource;
            console.log('[Chat] SSE подключение установлено');
        }).catch(error => {
            console.error('[Chat] Ошибка при отправке сообщения:', error);
            removeLoadingMessage(loadingMessage);
            addMessage('Ошибка подключения к серверу. Проверьте подключение к интернету.', false);
        });
    }

    /**
     * Загрузить историю сообщений для текущего чата
     */
    async function loadChatHistory(chatId) {
        console.log('[Chat] Загрузка истории сообщений для чата:', chatId);
        
        if (!window.API || !window.API.getChatMessages) {
            console.error('[Chat] API.getChatMessages не доступен');
            return;
        }

        try {
            const messages = await window.API.getChatMessages(chatId);
            console.log('[Chat] История сообщений получена:', messages);
            
            // Парсим и заполняем сообщения через JSON Parser
            if (window.JSONParser) {
                JSONParser.fillChatMessages(messages);
                hasStartedChat = true;
                chatWelcome.style.display = 'none';
                chatMessagesContainer.style.display = 'flex';
            } else {
                // Если JSON Parser недоступен, используем DOM Updater
                if (window.DOMUpdater) {
                    DOMUpdater.updateChatMessages(messages);
                    hasStartedChat = true;
                    chatWelcome.style.display = 'none';
                    chatMessagesContainer.style.display = 'flex';
                }
            }
        } catch (error) {
            console.error('[Chat] Ошибка при загрузке истории сообщений:', error);
        }
    }

    // Загружаем историю чата, если есть chatId в URL
    const urlParams = new URLSearchParams(window.location.search);
    const chatIdFromUrl = urlParams.get('chatId');
    if (chatIdFromUrl && window.API && window.API.isAuthenticated()) {
        currentChatId = chatIdFromUrl;
        loadChatHistory(chatIdFromUrl);
    }

    function addLoadingMessage() {
        const messageDiv = document.createElement('div');
        messageDiv.className = 'message bot-message loading-message';
        
        const messageContent = document.createElement('div');
        messageContent.className = 'message-content';
        
        const messageTextWrapper = document.createElement('div');
        messageTextWrapper.className = 'message-text-wrapper';
        
        const loadingText = document.createElement('div');
        loadingText.className = 'message-text';
        loadingText.textContent = 'Думаю...';
        loadingText.style.color = '#A9AAAC';
        loadingText.style.fontStyle = 'italic';
        
        messageTextWrapper.appendChild(loadingText);
        messageContent.appendChild(messageTextWrapper);
        messageDiv.appendChild(messageContent);
        chatMessages.appendChild(messageDiv);
        
        chatMessages.scrollTop = chatMessages.scrollHeight;
        return messageDiv;
    }

    function removeLoadingMessage(messageDiv) {
        if (messageDiv && messageDiv.parentNode) {
            messageDiv.parentNode.removeChild(messageDiv);
        }
    }

    function adjustTextareaHeight(textarea) {
        textarea.style.height = 'auto';
        textarea.style.height = Math.min(textarea.scrollHeight, 200) + 'px';
    }

    // Обработчики для начального поля ввода
    sendButton.addEventListener('click', () => sendMessage(promptInput));
    
    promptInput.addEventListener('keydown', function(e) {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            sendMessage(promptInput);
        }
    });

    promptInput.addEventListener('input', function() {
        adjustTextareaHeight(this);
    });

    // Обработчики для поля ввода в чате
    chatSendButton.addEventListener('click', () => sendMessage(chatInput));
    
    chatInput.addEventListener('keydown', function(e) {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            sendMessage(chatInput);
        }
    });

    chatInput.addEventListener('input', function() {
        adjustTextareaHeight(this);
    });
});
