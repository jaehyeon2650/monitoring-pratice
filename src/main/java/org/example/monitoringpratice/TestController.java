package org.example.monitoringpratice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TestController {

    private final MemberRepository memberRepository;

    @GetMapping("/")
    public String home() {
        log.info("come home");
        return "home";
    }

    @GetMapping("/home2")
    public String home2() {
        log.info("come home2");
        return "home2";
    }

    @PostMapping("/member")
    public String member() {
        log.info("come member");
        Member member = new Member("test");
        memberRepository.save(member);

        return member.getName();
    }
}
