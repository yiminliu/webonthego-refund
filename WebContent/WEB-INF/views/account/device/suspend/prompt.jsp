<%@ include file="/WEB-INF/views/include/header.jsp"%>

<div class="span-18 colborder">

  <form:form id="disconnect" cssClass="validatedForm" method="POST" commandName="accountDetail">
    <h3>Suspend Device</h3>

    <c:if test="${not empty requestScope['org.springframework.validation.BindingResult.accountDetail'].allErrors}">
      <div class="row clearfix">
        <div class="alert error">
          <h1>Please correct the following problems</h1>
          <form:errors path="deviceInfo.id" />
          <form:errors path="deviceInfo.value" />
          <form:errors path="deviceInfo.label" />
          <spring:bind path="accountDetail">
            <c:forEach items="${status.errorMessages}" var="error" varStatus="status">
              <span id="global.${status.index}.errors"><c:out value="${error}" /> </span>
            </c:forEach>
          </spring:bind>
        </div>
      </div>
    </c:if>

    <div class="notice">
      <h4>Attention!</h4>
      <p>You are about to suspend a device and the associated account.</p>
    </div>

    <p>After you click "Suspend" at the bottom of this page:</p>

    <ul style="font-size: 14px; margin: 10px 50px;">
      <li>Your device (ESN: ${deviceInfo.value}) and the account "${deviceInfo.label}" will be suspended immediately.</li>
      <li>If the device is currently connected, the service will stop after the current session.</li>
      <li>You will no longer be charged the monthly access fee for this account, the monthly access fee paid for this period expires on
        ${accessFeeDate.month}/${accessFeeDate.day}/${accessFeeDate.year}.</li>
      <li>If you restore this account before the current monthly access fee expires, you will be charged the next monthly access fee on
        ${accessFeeDate.month}/${accessFeeDate.day}/${accessFeeDate.year}.</li>
    </ul>

    <p>Are you sure you want to deactivate device ${deviceInfo.label}?</p>

    <div class="row clearfix" style="display: none;">
      <form:input path="deviceInfo.id" />
      <form:input path="deviceInfo.label" />
      <form:input path="deviceInfo.value" />
    </div>

    <div class="buttons">
      <a class="mBtn" href="<spring:url value="/devices" />">Cancel </a> <input type="submit" value="Suspend"></input>
    </div>
  </form:form>

</div>

<div class="span-6 last accountNav">
  <%@ include file="/WEB-INF/views/include/navigation/accountNav.jsp"%>
</div>

<%@ include file="/WEB-INF/views/include/footer.jsp"%>