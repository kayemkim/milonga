<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html>
<head>
</head>
<body>
<h1>
	Platform is ${platform}.
	Developer is ${developer.family[0]}
	<spring:eval expression="developer.family[0]"/>
</h1>

<P> 
<ul>
	<li>Developer
		<ul>
			<li>Name : ${developer.name}</li>
			<li>Company : <spring:eval expression="developer.company.companyName"/></li>
			<li>Family
				<ul>
					<li><spring:eval expression="developer.family[0]"/></li>
					<li><spring:eval expression="developer.family[1]"/></li>
					<li><spring:eval expression="developer.family[2]"/></li>
					<li><spring:eval expression="developer.family[3]"/></li>
				</ul>
			</li>
		</ul>
	</li>
</ul> 
</P>
</body>
</html>