package com.semestrovaya.demo.controllers;

import com.semestrovaya.demo.models.Count;
import com.semestrovaya.demo.models.Transaction;
import com.semestrovaya.demo.repo.CountRepository;
import com.semestrovaya.demo.repo.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.*;
import java.util.ArrayList;


@Controller
public class CountController {




    @Autowired
    CountRepository countRepository;

    @Autowired
    TransactionRepository transactionRepository;



    @GetMapping("/")
    public String countMain(Model model) throws IOException {
        File file = new File("log.txt");
        FileWriter pw = new FileWriter(file,true);
        pw.write("Visit a maim page");
        pw.write("\n");
        pw.close();


        Iterable<Count> counts = countRepository.findAll();
        model.addAttribute("counts",counts);

        return "count";
    }


    @GetMapping("/count/add")
    public String countAdd() throws IOException {

        File file = new File("log.txt");
        FileWriter pw = new FileWriter(file,true);
        pw.write("Starting add new count");
        pw.write("\n");
        pw.close();
        return "count-add";
    }

    @PostMapping("/count/add")
    public String postCountAdd(@RequestParam String name, @RequestParam String comment, Model model) throws IOException {

        Count count = new Count(name,comment);
        countRepository.save(count);
        File file = new File("log.txt");
        FileWriter pw = new FileWriter(file,true);
        pw.write("New count added");
        pw.write("\n");
        pw.close();
        return "redirect:/";
    }

    @GetMapping("/count/{id}")
    public String countDetails(@PathVariable(value = "id") long id, Model model) throws IOException {
        if(!countRepository.existsById(id))
        {
            return "redirect:/";
        }
        Iterable<Transaction> transactions = transactionRepository.findAll();
        ArrayList<Transaction> res = new ArrayList<>();
        int sum = 0;
        for (Transaction tr: transactions) {
            if(tr.getCount_id() == id) {
                if(tr.getStatus() == 1) {
                    res.add(tr);
                    sum = sum + tr.getSum();
                }
            }
        }
        model.addAttribute("transactions",res);
        model.addAttribute("id",id);
        model.addAttribute("sum",sum);
        File file = new File("log.txt");
        FileWriter pw = new FileWriter(file,true);
        pw.write("Watching transactions of count with id: "+ id);
        pw.write("\n");
        pw.close();
        return "count-details";
    }

    @GetMapping("/count/{id}/addtrans")
    public String addTransToCount(@PathVariable(value = "id") long id, Model model) throws IOException {
        model.addAttribute("count-id",id);
        File file = new File("log.txt");
        FileWriter pw = new FileWriter(file,true);
        pw.write("Starting add transaction to count with id: "+ id);
        pw.write("\n");
        pw.close();
        return "count-addtrans";
    }

    @PostMapping("/count/{id}/addtrans")
    public String postAddTransToCount(@PathVariable(value = "id") long id, @RequestParam int sum, Model model) throws IOException {
        Transaction tr = new Transaction(sum,id);
        transactionRepository.save(tr);
        String result = "redirect:/count/"+id;
        File file = new File("log.txt");
        FileWriter pw = new FileWriter(file,true);
        pw.write("Transaction to count with id: "+ id + " successfully added");
        pw.write("\n");
        pw.close();
        return result;
    }

    @GetMapping("/count/{id}/deletetrans")
    public String deleteTrans(@PathVariable(value = "id") long id,Model model) throws IOException {
        Transaction tr = transactionRepository.findById(id).orElseThrow();
        tr.setStatus(0);
        transactionRepository.save(tr);
        File file = new File("log.txt");
        FileWriter pw = new FileWriter(file,true);
        pw.write("Transaction with id: "+ id + " successfully deleted");
        pw.write("\n");
        pw.close();
        return "redirect:/";
    }

    @PostMapping("/count/{id}/close")
    public String closeCount(@PathVariable(value = "id") long id,Model model) throws IOException {

        Count co = countRepository.findById(id).orElseThrow();
        Iterable<Transaction> transactions = transactionRepository.findAll();
        ArrayList<Transaction> res = new ArrayList<>();
        for (Transaction tr: transactions) {
            if(tr.getCount_id() == id) {

                    res.add(tr);
            }
        }
        for(Transaction tr : res) {
            transactionRepository.delete(tr);
        }

        countRepository.delete(co);
        File file = new File("log.txt");
        FileWriter pw = new FileWriter(file,true);
        pw.write("Count with id: "+ id + "successfully closed");
        pw.write("\n");
        pw.close();
        return "redirect:/";

    }

}
