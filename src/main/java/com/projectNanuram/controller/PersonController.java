package com.projectNanuram.controller;

import com.projectNanuram.entity.Family;
import com.projectNanuram.entity.MobileNumbers;
import com.projectNanuram.entity.Person;
import com.projectNanuram.helper.ImageHelper;
import com.projectNanuram.helper.PropertiesResolver;
import com.projectNanuram.helper.ReferenceHelper;
import com.projectNanuram.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/person")
public class PersonController {

    @Autowired
    private PersonService personService;


    @GetMapping("/getPersonDetails/{personId}")
    public String getPersonDetails(@PathVariable("personId") String personId ,  Model model){
        Person person = personService.getPersonDetails(personId);
        String fullName = person.getFirstName();
        if(person.getMiddleName() != null && !person.getMiddleName().equals("")){
            fullName = fullName +" "+person.getMiddleName();
        }
        fullName = fullName + " " + person.getLastName();
        person.setFullName(fullName);

        person.setImgUrl(ImageHelper.getHostUrl(person.getImgUrl() , PropertiesResolver.getInstance().getImageProperties().get("person")));

        if(person.isSpeciallyAble()){
            person.setSpecialAbility("Yes");
        }
        else{
            person.setSpecialAbility("No");
        }

        System.out.println(person.getImgUrl());

        model.addAttribute(person);
        return "personProfile";
    }

    @GetMapping("/showUpdate/{personId}")
    public String showUpdate(@PathVariable("personId") String personId , Model model){

        Person person = personService.getPersonDetails(personId);
        String fullName = person.getFirstName();
        if(person.getMiddleName() != null && !person.getMiddleName().equals("")){
            fullName = fullName +" "+person.getMiddleName();
        }
        fullName = fullName + " " + person.getLastName();
        person.setFullName(fullName);

        person.setImgUrl(ImageHelper.getHostUrl(person.getImgUrl() , PropertiesResolver.getInstance().getImageProperties().get("personUpdate")));

        if(person.isSpeciallyAble()){
            person.setSpecialAbility("Yes");
        }
        else{
            person.setSpecialAbility("No");
        }

        System.out.println(person.getImgUrl());

        model.addAttribute("rd" , ReferenceHelper.referenceData());
        model.addAttribute(person);
//        model.addAttribute("mobileNumber" , person.getMobileNumbers());
        return "updatePerson";

    }

    @PostMapping("/updatePerson")
    public String updatePerson(@ModelAttribute("person") Person person ,  Model model){

        System.out.println("person id --> "+ person.getPersonId());
        String personId1 = person.getPersonId();
        System.out.println(person.getFamily());

        for(MobileNumbers mn : person.getMobileNumbers()){

            mn.setId(person.getPersonId()+person.getMobileNumbers().indexOf(mn));
            mn.setPerson(person);

        }
        if(person.getImageFile() == null){
            person.setImgUrl("ina.jpg");
        }

        personService.updatePerson(person);

        model.addAttribute("person" , person);
        return "redirect:/person/getPersonDetails/"+personId1;
    }

    @GetMapping("/getAllHeads")
    public String showHead(Model model){
        List<Person> heads = personService.getHeads();
        System.out.println(heads.size());
        String name="";
        for(Person person : heads){

            name = person.getFirstName();
            if(person.getMiddleName() != null && !person.getMiddleName().equals("")){

                name = name +" "+person.getMiddleName();

            }
            name = name +" "+person.getLastName();

            person.setFullName(name);
            person.setImgUrl(ImageHelper.getHostUrl(person.getImgUrl() , PropertiesResolver.getInstance().getImageProperties().get("family")));

        }
        model.addAttribute("heads",heads);
        return "showHeads";
    }

    @GetMapping("/showFamily/{personId}")
    public String getFamilyDetails(@PathVariable("personId") String personId , Model model){
        Family family = personService.getFamilyDetails(personId);
        System.out.println(family.getMembers().size());
        String name = "";
        for(Person person : family.getMembers()){

            name = person.getFirstName();

            if(person.getMiddleName() != null && !person.getMiddleName().equals("")){
                name = name +" "+person.getMiddleName();
            }

            name = name +" "+person.getLastName();
            person.setFullName(name);
            person.setImgUrl(ImageHelper.getHostUrl(person.getImgUrl() , PropertiesResolver.getInstance().getImageProperties().get("family")));
        }

        model.addAttribute(family);
        return "index";
     }

}
