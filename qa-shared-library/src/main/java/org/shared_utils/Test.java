package org.shared_utils;


public class Test {

    public static void main(String[] args) throws InterruptedException {

        Driver.getDriver().get("https://www.google.com/");
        Thread.sleep(1000);
        Driver.closeDriver();
    }
}
