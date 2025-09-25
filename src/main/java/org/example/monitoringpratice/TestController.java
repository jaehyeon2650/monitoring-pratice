package org.example.monitoringpratice;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final MemberRepository memberRepository;

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/home2")
    public String home2() {
        return "home2";
    }

    @PostMapping("/member")
    public String member() {
        Member member = new Member("test");
        memberRepository.save(member);

        return member.getName();
    }
}
