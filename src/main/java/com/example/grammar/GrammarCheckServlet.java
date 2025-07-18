package com.example.grammar;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(urlPatterns = {"/checkGrammar", "/"})
public class GrammarCheckServlet extends HttpServlet {

    private static final String GEMINI_API_KEY = "AIzaSyBKqnIdy5Fp8SqBTlbkxSKZXigAa6Yo2TY";
    private static final String GEMINI_ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=";

    private static final Logger logger = Logger.getLogger(GrammarCheckServlet.class.getName());
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");
        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");

        String text = req.getParameter("text");

        if (text == null || text.trim().isEmpty()) {
            req.setAttribute("error", "Vui lòng nhập câu tiếng Nhật cần kiểm tra.");
            req.getRequestDispatcher("/index.jsp").forward(req, resp);
            return;
        }

        if (GEMINI_API_KEY == null || GEMINI_API_KEY.trim().isEmpty()) {
            req.setAttribute("error", "API key Gemini chưa được cấu hình.");
            req.getRequestDispatcher("/index.jsp").forward(req, resp);
            return;
        }

        try {
            String result = callGeminiAPI(text);
            req.setAttribute("correctedText", result);
            req.setAttribute("message", "Kết quả kiểm tra:");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Gemini API error", e);
            req.setAttribute("error", "Lỗi từ Gemini API: " + e.getMessage());
        }

        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }

    private String callGeminiAPI(String userText) throws IOException, InterruptedException {
        ObjectNode payload = mapper.createObjectNode();
        var contents = payload.putArray("contents");

        ObjectNode message = mapper.createObjectNode();
        message.put("role", "user");

        var parts = message.putArray("parts");
        ObjectNode partNode = mapper.createObjectNode();
        partNode.put("text",
            "Tôi là người Việt, đang học tiếng Nhật.\n"
            + "Bạn hãy giúp kiểm tra câu tiếng Nhật sau có đúng không.\n"
            + "Nếu sai, hãy:\n"
            + "- Trả về câu đã được sửa ở dòng đầu tiên.\n"
            + "- Sau đó, ở các dòng tiếp theo, giải thích ngắn gọn các lỗi sai và cách sửa. (ví dụ: わ → は)\n\n"
            + "Câu:「" + userText + "」"
        );
        parts.add(partNode);
        contents.add(message);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GEMINI_ENDPOINT + GEMINI_API_KEY))
                .header("Content-Type", "application/json; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(payload.toString(), StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        logger.info("Gemini API Status: " + response.statusCode());
        logger.info("Gemini Response: " + response.body());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Lỗi Gemini API: " + response.body());
        }

        JsonNode root = mapper.readTree(response.body());
        if (!root.has("candidates") || root.path("candidates").isEmpty()) {
            throw new RuntimeException("Phản hồi từ Gemini không hợp lệ.");
        }

        return root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
    }
}
