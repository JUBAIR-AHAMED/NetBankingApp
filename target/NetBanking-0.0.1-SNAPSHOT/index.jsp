<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>ABA</title>
	<style>
		body {
			height: 100%;
			display: flex;
			flex-direction: column;
			align-items: center;
		}
		
		.navbar{
			display: flex;
			width: 80%;
			flex-direction: row;
			justify-content: space-between;
			height: 100px;
			padding: 10px;
			border: black;
			border: double;
    		border-radius: 8px;
    		margin: 10px;
		}
		
		.navbarleft{
			height: 100%;
			display: flex;
			flex-direction: row;
			display: flex;
    		align-items: center;
    		padding-left: 30px;
		}
		
		.navbarleft span{
			font-size: 35px;
			font-family: sans-serif;
			color: black;
			font-weight: 600;
		}
		
		.navbarleft img{
			height: 80px;
			width: 80px;
		}
		
		.navbarright{
			height: 100%;
			display: flex;
    		align-items: center;
		}
		
		.navbarright a{
			font-size: 25px;
			font-weight: 500;
			padding-right: 50px;
			color: black;
			text-decoration: none;
		}
		
		.navbarright a:hover{
			text-decoration: underline;
		}
		.we {
			display: flex;
			width: 70%;
			justify-content: space-between;
			margin: 10px;
			padding: 50px;
		}
		
		.we p {
			width: 40%;
			font-size: 25px;
			text-align: justify;
			color: black;
		}
		
		.we img {
			width: 550px;
			height: 300px;
			border-radius: 20px
		}
	</style>
</head>
<body>
	<nav class="navbar">
		<div class="navbarleft">
			<img alt="logo" src="https://cdn.iconscout.com/icon/free/png-512/free-cdn-icon-download-in-svg-png-gif-file-formats--content-delivery-network-global-whcompare-blue-green-web-hosting-pack-communication-icons-1496574.png?f=webp&w=256">
			<span>BANK OF UTOPIA</span>
		</div>
		<div class="navbarright">
			<a href="signin.jsp">Sign In</a>
		</div>
	</nav>
	<div class="we">
		<img alt="bank" src="https://www.forbes.com/advisor/wp-content/uploads/2021/04/Loan.jpg">
		<p>
		Welcome to BANK OF UTOPIA, where your financial needs are our top priority! At BANK OF UTOPIA, we pride ourselves on offering exceptional services that make banking simple, secure, and convenient. Whether you're looking to open a savings account, apply for a loan, or manage your investments, we've got you covered.
		</p>	
	</div>
	<div class="we">
		<p>
		Flexible Repayment Terms: Choose from a variety of repayment options that suit your budget and lifestyle.
Competitive Interest Rates: We offer some of the most competitive rates in the market, ensuring you save more over time.
Quick and Easy Application: Our streamlined application process allows you to get approved fast, with minimal paperwork.
		</p>	
		<img alt="bank" src="https://paytmblogcdn.paytm.com/wp-content/uploads/2023/10/Blog_Paytm_Mortgage-Loan-How-to-Apply-Interest-Rate-1-800x500.jpg">
	</div>
</body>
</html>