package com.economic.survey.web;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@RestController
public class GraphApiController {

    @GetMapping("/api/graph-data")
    public String getGraphData() {
        // 1. Python 서버 주소 (로컬에서 실행 중인 FastAPI/Flask 주소)
        // 만약 Python 코드가 루트("/")에서 데이터를 준다면 주소는 "http://localhost:8000"
        // 특정 경로("/data" 등)라면 그에 맞춰 수정해야 합니다.
        String pythonServerUrl = "http://localhost:8000";

        // 2. Spring이 Python 서버로 요청을 보내 데이터 가져오기 (RestTemplate 사용)
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(pythonServerUrl, String.class);

        // 3. 가져온 JSON 데이터를 브라우저(JS)에게 그대로 전달
        return result;
    }
}