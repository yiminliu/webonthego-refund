<link rel="stylesheet" href="<spring:url value='/static/styles/admin/controlBar.css' />" type="text/css" />
<script type="text/javascript" src="<spring:url value='/static/javascript/pages/admin/controlBar.js' />"></script>

<div id="admin_control_bar">
  <!-- ADMIN LOGO -->
  <div class="logo">
    <a href="<spring:url value="/home" />"> <img src="<spring:url value='/static/images/logo/logo_admin_sm.png' />" />
    </a>
  </div>

  <!-- LOGOUT/ID -->
  <div class="logout">
    <c:choose>
      <c:when test="${CONTROLLING_USER.userId != -1}">
        <b>Internal:</b>
        <c:out value="${CONTROLLING_USER.username}" />
      </c:when>
      <c:otherwise>
        <sec:authentication property="principal.authorities" />
        <sec:authentication property="principal.username" />
      </c:otherwise>
    </c:choose>
    <a href="<spring:url value='/logout' />">Logout</a>
  </div>

  <!-- SET CURRENTLY VIEWED USER -->
  <c:choose>
    <c:when test="${USER.userId > 0}">
      <c:set var="currentUser" value="${sessionScope.USER.userId} ${sessionScope.USER.email}" />
    </c:when>
    <c:otherwise>
      <c:set var="currentUser" value="Search by Email or ID" />
    </c:otherwise>
  </c:choose>
  <div class="currentUser hidden">
    <a href="<spring:url value="/account" />">${currentUser} </a>
  </div>

  <!-- SEARCH FORM -->
  <form id="adminControl" method="post" action="<spring:url value="/search" />">

    <div style="float: left; padding-right: 5px;">
      <input name="admin_search_id" id="admin_search_id" type="text" class="hidden" /> <input autocomplete="off" name="admin_search_param"
        id="admin_search_param" type="text" placeholder="${currentUser}" />
      <div id="admin_search_results" class="search_results_box"></div>
    </div>

    <div style="text-align: left;">
      <input id="adminControl_button_submit" type="submit" value="Go" class="mBtn" /> <input id="adminControl_button_reset" type="reset" class="mBtn" /> <a
        href="<spring:url value="/support/ticket" />" class="mBtn">Tickets</a>
    </div>

  </form>

</div>