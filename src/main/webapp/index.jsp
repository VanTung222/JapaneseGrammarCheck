<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Trình kiểm tra ngữ pháp tiếng Nhật</title>
    <style>
        body {
            font-family: 'Segoe UI', sans-serif;
            background: #f4f6fb;
            color: #333;
            max-width: 800px;
            margin: 50px auto;
            padding: 20px;
            border-radius: 12px;
            background-color: white;
            box-shadow: 0 8px 16px rgba(0,0,0,0.1);
        }
        h1 {
            color: #4f8cff;
        }
        textarea {
            width: 100%;
            height: 150px;
            padding: 10px;
            font-size: 16px;
            border: 1px solid #ccc;
            border-radius: 8px;
        }
        .btn {
            margin-top: 10px;
            background-color: #4f8cff;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 8px;
            cursor: pointer;
        }
        .message {
            margin-top: 20px;
            padding: 15px;
            border-radius: 8px;
            background-color: #f0f4ff;
        }
        .error {
            background-color: #ffe5e5;
            color: #cc0000;
        }
        pre {
            white-space: pre-wrap;
            word-break: break-word;
        }
    </style>
</head>
<body>
    <h1>Trình kiểm tra ngữ pháp tiếng Nhật (Gemini)</h1>
    <form method="post" action="checkGrammar">
        <label for="text">Nhập câu tiếng Nhật:</label>
        <textarea name="text" placeholder="Nhập câu cần kiểm tra..."></textarea>
        <br/>
        <button class="btn" type="submit">Kiểm tra</button>
    </form>

    <c:if test="${not empty message}">
        <div class="message">
            <strong>${message}</strong><br/>
            <pre>${correctedText}</pre>
        </div>
    </c:if>

    <c:if test="${not empty error}">
        <div class="message error">
            <strong>Lỗi:</strong><br/>
            <p>${error}</p>
        </div>
    </c:if>
</body>
</html>
