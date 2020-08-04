package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.RsEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
public class RsController {
  private List<RsEvent> rsList = Arrays.asList(new RsEvent("第一条事件", "经济"),
          new RsEvent("第二条事件", "文化"), new RsEvent("第三条事件", "政治"));

  @GetMapping("rs/list")
  public List<RsEvent> getAllInList(@RequestParam(required = false) Integer startIndex,
                                    @RequestParam(required = false) Integer endIndex) {
    if (startIndex == null || endIndex == null) {
      return rsList;
    }
    return rsList.subList(startIndex - 1, endIndex);
  }

}
