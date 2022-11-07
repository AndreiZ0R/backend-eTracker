package com.example.etracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

@RestController
public class Controller {

    @Autowired
    private Repo repo;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index() {
        return "Belimiai cuaiele...";
    }

    @RequestMapping(value = "/items/delete/{index}", method = RequestMethod.DELETE)
    public int deleteItem(@PathVariable("index") int index) {
        return repo.delete(index, "items");
    }

    @RequestMapping(value = "/{table}/get/{index}", method = RequestMethod.GET)
    public Map<String, Object> getFromTable(@PathVariable("index") int index,
                                                  @PathVariable("table") String table) {
        return repo.getOne(index, table);
    }

    @RequestMapping(value = "/{table}/get", method = RequestMethod.GET)
    public List<Map<String, Object>> getFromTable(@PathVariable("table") String table) {
        return repo.getAll(table);
    }

    @RequestMapping(value = "/{table}/update/{index}?{colName}={value}", method = RequestMethod.PATCH)
    public int updateFromTable(@PathVariable("index") int index,
                               @PathVariable("table") String table,
                               @PathVariable("colName") String colName,
                               @PathVariable("value") String value) {

        return repo.update(index, colName, value, table);
    }

    @RequestMapping(value = "/items/create", method = RequestMethod.POST)
    @ResponseBody
    public int createItem(@RequestParam("name") String name,
                          @RequestParam("dsc") String dsc,
                          @RequestParam("cat") String cat,
                          @RequestParam("lrev") String lrev,
                          @RequestParam("interval") int interval,
                          @RequestParam("loc") String loc) {
        return repo.createItem(name, dsc, cat, lrev, interval, loc);
    }

    @RequestMapping(value = "/workers/create?fname={fname};cname={cname};phone={phone};prefix={prefix};addr={addr};active={active}", method = RequestMethod.POST)
    public int createWorker(@PathVariable("fname") String fname,
                            @PathVariable("cname") String cname,
                            @PathVariable("phone") int phone,
                            @PathVariable("prefix") int prefix,
                            @PathVariable("addr") String addr,
                            @PathVariable("active") int active) {
        return repo.createWorker(fname, cname, phone, prefix, addr, active);
    }

    @RequestMapping(value = "/reports/create?iid={iid};wid={wid};type={type};date={date};link={link}", method = RequestMethod.POST)
    public int createReport(@PathVariable("iid") int iid,
                            @PathVariable("wid") int wid,
                            @PathVariable("type") String type,
                            @PathVariable("date") String date,
                            @PathVariable("link") String link) {
        return repo.createReport(iid, wid, type, date, link);
    }

    @RequestMapping(value = "/accounts/create", method = RequestMethod.POST)
    public int createAccount(@RequestParam("fname") String fname,
                             @RequestParam("lname") String lname,
                             @RequestParam("uname") String uname,
                             @RequestParam("email") String email,
                             @RequestParam("pwd") String pwd,
                             @RequestParam("role") String role,
                             @RequestParam("wid") int wid) throws NoSuchAlgorithmException {
        return repo.createAccount(fname, lname, uname, email, pwd, role, wid);
    }

    @RequestMapping(value = "/login/{username}/{password}", method = RequestMethod.POST)
    public int login(@PathVariable("username") String username, @PathVariable("password") String password) throws NoSuchAlgorithmException {
        return repo.login(username, password);
    }
}
