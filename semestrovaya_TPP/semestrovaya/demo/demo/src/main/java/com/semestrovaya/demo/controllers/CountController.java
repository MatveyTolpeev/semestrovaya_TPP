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

import java.util.ArrayList;
import java.util.Optional;

@Controller
public class CountController {

    @Autowired
    CountRepository countRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @GetMapping("/")
    public String countMain(Model model) {
        Iterable<Count> counts = countRepository.findAll();
        model.addAttribute("counts",counts);
        return "count";
    }


    @GetMapping("/count/add")
    public String countAdd() {
        return "count-add";
    }

    @PostMapping("/count/add")
    public String postCountAdd(@RequestParam String name, @RequestParam String comment, Model model) {
        Count count = new Count(name,comment);
        countRepository.save(count);
        return "redirect:/";
    }

    @GetMapping("/count/{id}")
    public String blogDetails(@PathVariable(value = "id") long id, Model model) {
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
        return "count-details";
    }

    @GetMapping("/count/{id}/addtrans")
    public String addTransToCount(@PathVariable(value = "id") long id, Model model) {
        model.addAttribute("count-id",id);
        return "count-addtrans";
    }

    @PostMapping("/count/{id}/addtrans")
    public String postAddTransToCount(@PathVariable(value = "id") long id, @RequestParam int sum, Model model) {
        Transaction tr = new Transaction(sum,id);
        transactionRepository.save(tr);
        String result = "redirect:/count/"+id;
        return result;
    }

    @GetMapping("/count/{id}/deletetrans")
    public String deleteTrans(@PathVariable(value = "id") long id,Model model) {
        Transaction tr = transactionRepository.findById(id).orElseThrow();
        tr.setStatus(0);
        transactionRepository.save(tr);
        return "redirect:/";
    }

    @PostMapping("/count/{id}/close")
    public String closeCount(@PathVariable(value = "id") long id,Model model) {

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
        return "redirect:/";

    }

}
