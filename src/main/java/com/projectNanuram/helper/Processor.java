package com.projectNanuram.helper;

import com.projectNanuram.entity.Address;
import com.projectNanuram.entity.Family;
import com.projectNanuram.entity.MobileNumbers;
import com.projectNanuram.entity.Person;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Processor {

    public static List<Person> personprocessor(List<Person> personList, Family family) {

        try {
            if (personList != null) {
                for (Person person : personList) {
                    if (person != null) {
                        int age = AgeCalculator.age(person.getDOB());
                        person.setAge(age);

                        ageProcessor(person);

                        String id = person.getPersonId();
                        if (id == null || id.isEmpty()) {

                            String pId = IdentityHelper.personIdGenerator();
                            person.setPersonId(pId);
                        }

//                            if (person.getFamily() == null || person.getFamily().getFamilyId()==null || person.getFamily().getFamilyId().isEmpty())
                        person.setFamily(family);

                        List<MobileNumbers> mobileNumbersList = person.getMobileNumbers();

                        person.setMobileNumbers(mobilenumberProcessor(mobileNumbersList, person));

                        imageProcessor(person);

                        if (person.getSpecialAbility().equalsIgnoreCase("True")) {

                            person.setSpeciallyAble(true);

                        } else {
                            person.setSpeciallyAble(false);
                        }

                    }

                }

            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return personList;
    }

//----------------------------------------------------------------------------------------------------------------------

    public static void imageProcessor(Person person) {
        String path = null;
        try {

            CommonsMultipartFile file = person.getImageFile();

            if (file != null && file.getSize() > 0) {

                String filename = file.getOriginalFilename();
                System.out.println(file.getOriginalFilename());

                String[] str = filename.split("\\.");
                str[1] = "." + str[1];

                String baseDirPath = PropertiesResolver.getInstance().getBaseFilePath();
                String newFileName = person.getPersonId() + str[1];
                path = baseDirPath + newFileName;

                byte[] bytes = file.getBytes();
                FileOutputStream fos = new FileOutputStream(path);
                fos.write(bytes);
                fos.close();

                person.setImgUrl(newFileName);
                ImageHelper.resizeImage(newFileName);
            } else {
                person.setImgUrl("ina.jpg");
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
//----------------------------------------------------------------------------------------------------------------------

    private static List<MobileNumbers> mobilenumberProcessor(List<MobileNumbers> mobileNumbersList, Person person) {
        List<MobileNumbers> newlist = new ArrayList<>();
        if (!mobileNumbersList.isEmpty()) {
            for (MobileNumbers number : mobileNumbersList) {
                if (number.getMobileNumber() != null && !number.getMobileNumber().isEmpty()) {

                    if (number.getPrimaryString().equalsIgnoreCase("true")) {
                        number.setPrimary(true);
                    } else {
                        number.setPrimary(false);
                    }
                    number.setPerson(person);
                    number.setId(person.getPersonId() + mobileNumbersList.indexOf(number));
                    newlist.add(number);
                }
            }

        }

        return newlist;

    }
//----------------------------------------------------------------------------------------------------------------------

    private static void ageProcessor(Person person) {

        int age = person.getAge();
        if (person.getGender().equalsIgnoreCase("Male")) {

            System.out.println(person.getDOB().getClass());
            System.out.println(person.getDOB());
            if (age < 18) person.setBoy(true);
            else person.setMan(true);
            if (person.isMan() && age >= 60) person.setSenior(true);
        }

        if (person.getGender().equalsIgnoreCase("Female")) {
            if (age < 18) person.setGirl(true);
            else person.setWoman(true);
            if (person.isWoman() && age >= 60) person.setSenior(true);

        }
//----------------------------------------------------------------------------------------------------------------------

        if (person.getStatus().equalsIgnoreCase("head")) {
            person.setHead(true);
        }

    }
//----------------------------------------------------------------------------------------------------------------------

    public static List<Address> addressProcessor(List<Address> addressList, Family family) {

        List<Address> newList = new ArrayList<>();

        if (!addressList.isEmpty()) {

            for (Address address : addressList) {

                if (!address.getFirstName().isEmpty() && (!address.getAddress1().isEmpty() || !address.getAddress2().isEmpty())) {
                    address.setFamily(family);
                    newList.add(address);

                }
            }

        }
        return newList;
    }

//----------------------------------------------------------------------------------------------------------------------

    public static void imageProcessor(Person person, CommonsMultipartFile file) {

        String name = file.getOriginalFilename();
        System.out.println("File name --> " + name);

        try {

            if(person.getImgUrl().equalsIgnoreCase("ina.jpg")){
                person.setImageFile(file);
                imageProcessor(person);
            }
            else{
                    System.out.println("Inside imageProcessor(personId , Multipartfile)");
                    System.out.println("CommonsMultipartFile name = " + file.getOriginalFilename());
                    deleteFile(person.getImgUrl());
                    person.setImageFile(file);
                    imageProcessor(person);


            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//----------------------------------------------------------------------------------------------------------------------

    public static void deleteFile(String fileName) {

        boolean flag = false;
        Map<String, List<String>> imageProperties = PropertiesResolver.getInstance().getImageProperties();
        System.out.println("Inside deleteFileMethod");

        String[] filesToDelete = {
                "H:\\uploads\\" + fileName,
                ImageHelper.getFilePath(fileName , imageProperties.get("person")),
                ImageHelper.getFilePath(fileName , imageProperties.get("family"))

        };

        try {

            for (String files : filesToDelete) {

                if(Files.deleteIfExists(Paths.get(files))) {
                    flag = true;
                    System.out.println("file deleted ---> " + files);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
