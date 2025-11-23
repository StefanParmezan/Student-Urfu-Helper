package urfu.student.helper.security.parser;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.server.ResponseStatusException;
import urfu.student.helper.db.student.StudentEntity;
import urfu.student.helper.db.student.dto.StudentRegistryDTO;

import java.util.List;

public class ProfileParser extends SeleniumParser{

    public StudentRegistryDTO parseStudentProfile(String email, String password){
        login(email, password);
        getDriver().get("https://elearn.urfu.ru/user/profile.php");
        String fio = getDriver().findElement(By.className("h2")).getText();
        String studentEmail = getDriver().findElement(By.xpath("//a[contains(@href, 'mailto')]")).getText();
        String timeZone = getDriver().findElement(By.xpath("//li[@class='contentnode']//dd")).getText();
        String educationStatus = getDriver().findElement(By.xpath("//dd[@id='yui_3_17_2_1_1763856197350_35']")).getText();
        String academicGroup = getDriver().findElement(By.xpath("//dt[text()='Academic_group']/following-sibling::dd")).getText();
        String studentNumber = getDriver().findElement(By.xpath("//li[@class='contentnode']//dt[text()='Student_number']/following-sibling::dd")).getText();
        StudentRegistryDTO studentRegistryDTO = new StudentRegistryDTO(fio, timeZone, educationStatus, academicGroup, studentNumber, studentEmail);
        return studentRegistryDTO;
    }

    public void login(String email, String password){
        getDriver().get("https://elearn.urfu.ru/my/");
        try {
            getDriver().findElement(By.id("userNameInput")).sendKeys(email);
            getDriver().findElement(By.id("passwordInput")).sendKeys(password);
            getDriver().findElement(By.id("submitButton")).click();
        }
        catch (Exception e){
            throw new ResponseStatusException(HttpStatus.valueOf(400), "Такого аккаунта elearn не существует");
        }
    }


}
