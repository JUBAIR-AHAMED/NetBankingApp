<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sign In</title>
    <style>
        body {
            display: flex;
            flex-direction: row;
            height: 98vh;
        }
        
        .left {
        	width: 50%;
        	background-image: url('https://etimg.etb2bimg.com/photo/104637257.cms');
        	background-size: cover; 
			background-position: center; 
			background-repeat: no-repeat;
			border-radius: 16px;
        }
        .right {
	        margin: 0;
            padding: 0;
            display: flex;
            width: 50%;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            height: 100vh;
            background-color: white;
            font-family: Arial, sans-serif;}
        
        .name span {
        	font-size: 50px;
        	font-family: sans-serif;
        	font-weight: 500;
        }
        
        .container {
            background-color: #eaf2f6;
            padding: 40px;
            border-radius: 12px;
            /*box-shadow: 0px 10px 30px rgba(0, 0, 0, 0.1);*/
            box-shadow: 0px 0px 10px #eaf2f6;
            width: 100%;
            max-width: 400px;
            height: 30%;
            text-align: center;
            display: flex;
            flex-direction: column;
            justify-content: center;
        }

        .container span {
            color: black;
            font-size: 30px;
            font-weight: 600;
            font-family: fantasy;
        }

        .input-group {
            margin-bottom: 20px;
            text-align: left;
        }

        .input-group label {
            color: #555;
            margin-bottom: 5px;
        }

        .input-group input {
            width:  calc(100% - 20px);
            padding: 10px;
            border-radius: 8px;
            border: 1px solid #ddd;
            font-size: 16px;
        }

        .btn {
            width: 100%;
            padding: 12px;
            background: linear-gradient(to right, #6a11cb, #2575fc); /* Matching gradient for the button */
            border: none;
            color: white;
            font-size: 18px;
            border-radius: 8px;
            cursor: pointer;
        }
    </style>
</head>
<body>
	<div class="left">
	</div>
	<div class="right">
	<div class="name">		
        <span>BANK OF UTOPIA</span>
	</div>
    <div class="container">
        <form action="userlogin" method="post">
        	<span>Sign In</span>
            <div class="input-group">
                <label for="username">Username</label>
                <input type="text" id="username" name="username" required>
            </div>
            <div class="input-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" required>
            </div>
            <button type="submit" class="btn" value="Login">Sign In</button>
        </form>
    </div>
	</div>
</body>
</html>
