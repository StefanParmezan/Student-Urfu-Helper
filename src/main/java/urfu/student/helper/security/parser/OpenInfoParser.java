package urfu.student.helper.security.parser;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import urfu.student.helper.security.dto.CourseDto;

public class OpenInfoParser extends SeleniumParser {
    public Flux<CourseDto> getCourses() {
        getDriver().get("https://elearn.urfu.ru/");

        return Flux.fromIterable(getDriver().findElements(By.className("category")))
                .map(element -> element.findElement(By.tagName("a")))
                .flatMap(this::getRecursive)
                .flatMap(this::courseFromElement);
    }

    public Flux<WebElement> getRecursive(WebElement category) {
        String url = category.getDomAttribute("href");
        if (
                url == null || url.isEmpty()
        ) {
            return Flux.from(Mono.just(category));
        }
        getDriver().get(url);

        return Flux.fromIterable(getDriver().findElements(By.className("category")))
                .map(element -> element.findElement(By.tagName("a")))
                .flatMap(this::getRecursive);
    }

    private Mono<CourseDto> courseFromElement(WebElement element) {
        return Mono.just(new CourseDto(
                element.getText(),
                null,
                element.getDomAttribute("href")
        )).onErrorResume(throwable -> Mono.empty());
    }
}
