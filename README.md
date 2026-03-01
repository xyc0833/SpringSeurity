## 遇到403的报错 

1 先看一看是不是 _csrf 这个东西有没有配置好

SpringSecurity自带了csrf防护，需求我们在POST请求中携带页面中的csrfToken才可以，
否则一律进行拦截操作，这里我们可以将其嵌入到页面中，随便找一个地方添加以下内容：
<input type="text" th:id="${_csrf.getParameterName()}" th:value="${_csrf.token}" hidden>

有没有关闭 _csrf