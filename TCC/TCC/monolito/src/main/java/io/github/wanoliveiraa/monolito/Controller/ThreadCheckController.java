package io.github.wanoliveiraa.monolito.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api/status")
public class ThreadCheckController {

    @GetMapping("/thread-type")
    public Map<String, Object> checkThreadType() {
        Thread currentThread = Thread.currentThread();

        return Map.of(
                "isVirtual", currentThread.isVirtual(),
                "threadName", currentThread.getName(),
                "message", "Se 'isVirtual' for TRUE, as VTs estão ativas."
        );
    }
}
