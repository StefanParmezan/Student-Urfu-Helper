document.addEventListener('DOMContentLoaded', function() {
    // Загружаем данные с backend после загрузки страницы
    if (window.API && window.API.isAuthenticated()) {
        console.log('[Script] Пользователь авторизован, загружаем данные с backend...');
        
        // Загружаем профиль и курсы пользователя через JSON Parser
        if (window.JSONParser) {
            JSONParser.loadAndFillProfile();
            // Загружаем курсы на главной странице
            if (window.location.pathname.includes('index.html') || window.location.pathname === '/') {
                JSONParser.loadAndFillCourses();
            }
        }
    } else {
        console.log('[Script] Пользователь не авторизован');
    }

    const navCards = document.querySelectorAll('.nav-card');
    const courseCards = document.querySelectorAll('.course-card');
    
    navCards.forEach(card => {
        card.addEventListener('click', function(e) {
            // Если это ссылка, не предотвращаем переход
            if (this.tagName === 'A') {
                return;
            }
            
            e.preventDefault();
            navCards.forEach(c => c.classList.remove('active'));
            this.classList.add('active');
            
            const cardText = this.querySelector('.nav-description').textContent;
            console.log('Нажата кнопка:', cardText);
        });
    });
    
    courseCards.forEach(card => {
        card.addEventListener('click', function() {
            const courseName = this.querySelector('.course-name').textContent;
            console.log('Выбран курс:', courseName);
            alert(`Выбран курс: ${courseName}`);
        });
    });
    
    const viewAll = document.querySelector('.view-all');
    if (viewAll) {
        viewAll.addEventListener('click', function() {
            console.log('Нажата кнопка "Смотреть все"');
            alert('Показать все курсы');
        });
    }
    
    // Устанавливаем ширину линии равной ширине текста
    const sectionHeaders = document.querySelectorAll('.section-header');
    sectionHeaders.forEach(header => {
        const headerText = header.querySelector('.header-text');
        const headerLine = header.querySelector('.header-line');
        if (headerText && headerLine) {
            const textWidth = headerText.offsetWidth;
            headerLine.style.width = textWidth + 'px';
        }
    });
});