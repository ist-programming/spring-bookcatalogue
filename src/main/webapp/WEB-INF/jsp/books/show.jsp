<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%--
  Creating own tags and simple templating system : http://stackoverflow.com/questions/1296235/jsp-tricks-to-make-templating-easier
--%>
<t:mainLayout title="Book \"${book.name}\"">
  <div class="book-page">
    <h1 class="book-name">${book.name}</h1>
    <div class="book-field">
      <span class="field-name">Year:</span>
      <span class="field-value">${book.year}</span>
    </div>
    <div class="book-field">
      <span class="field-name">ISBN:</span>
      <span class="field-value">${book.name}</span>
    </div>
    <div class="book-field">
      <span class="field-name">Link:</span>
      <span class="field-value"><a href="${book.url}" target="_blank">${book.url}</a></span>
    </div>
    <div class="book-description">${book.description}</div>
  </div>
</t:mainLayout>