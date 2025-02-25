<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>My Account</title>
<style>
	.maincont {
		display: flex;
		flex-direction: row;
		height: 98vh;
		max-height: 98vh;
        background: linear-gradient(135deg, #62a3a8, #004A90);
        border-radius: 10px;
	}
	
	.bar {
		width: 20%;
        background: linear-gradient(135deg, #62a3a8);
		/* height: 100%; */ 
		display: flex;
		flex-direction: column;
		align-items: center;
		/* justify-content: space-between; */
        border-radius: 8px;
	}	
    
    hr {
            /* border: none;  */
            height: 0.5px;
            width: 80%; 
            background-color: white; 
            margin: 20px 0; 
        }

    .profile {
        display: flex;
        flex-direction: column;
        align-items: center;
        height: 30vh;
        justify-content: center;
        transition: transform 0.3s ease;
    }

    .profile:hover{
        transform: scale(0.9);
    }

    .profile span{
        font-size: 30px;
        color: white;
    }

	.profileimg {
		width: 150px;
        height: 150px;
		border-radius: 50%;
        object-fit: cover;
        border: 3px solid #ddd;
        margin-bottom: 10px;
	}
	
	.accounts {
		width: 100%;
        margin: 20px;
        background-color: #ddd;
        border-radius: 15px;
	}
    
    .options {
        width: 100%;
        /* height: 35vh; */
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
    }
    
    .options span {
        transition: transform 0.3s ease;
    }
    .options span:hover {
        transform: scale(1.2);
    }

    .options span {
        background-color: white;
        border-radius: 15px;
        width: 65%;
        height: 5vh;
        display: flex;
        justify-content: center;
        align-items: center;
        margin-top: 30px;
        margin-bottom: 30px;
    }

    .options a {
        color: #1b4c8d;
        text-decoration: none;
        font-size: 25px;
    }

    .options a:active, a:focus {
            color: #1b4c8d; /* Color remains the same on click/focus */
            outline: none; /* Optional: remove the outline on focus */
        }
    
    .logout {
        width: 120px;
        height: 35px;
        display: flex;
        justify-content: center;
        align-items: center; 
        font-size: 25px;
        color: white;
        background-color: red;
        margin: 20px;
        font-weight: 900;
        border-radius: 9px;
        transition: transform 0.3s ease;
    }

    .logout:hover{
        transform: scale(1.2);
    }

    .header {
        height: 100px;
        display: flex;
        flex-direction: row;
        /* justify-content: center; */
        align-items: center;
        border-bottom: 2px solid transparent;
        border-color: #62a3a8;
        background-color: white;
        border-radius: 16px;
    }

    .header img {
        width: 70px;
        height: 70px;
        padding-left: 20px;
    }

    .header span {
        font-size: 50px;
        padding-left: 20px;
        color: black;
        font-weight: 900;
        font-family: 'Courier New', Courier, monospace;
    }

    .card {
        width: 450px;
        height: 250px;
        background: linear-gradient(135deg, #62a3a8, #004A90);
        color: white;
        border-radius: 16px;
        margin: 15px;
        display: flex;
        flex-direction: column;
        transition: transform 0.3s ease;
        justify-content: space-between;
        /* align-items: center; */
    }

    .card:hover{
        box-shadow: #62a3a8;
        transform: scale(1.02);
    }

    .cardnumber {
        font-size: 35px;
        align-self: center;
    }
    .cardheader{
        height: 25px;
        align-self: flex-end;
        display: flex;
        flex-direction: row;
        align-items: center;
        justify-content: center;
        padding: 10px;
    }

    .cardheader img {
        width: 25px;
        height: 25px;
    }

    .balance {
        display: flex;
        flex-direction: column;
        padding: 10px;
    }

    .balancerupee {
        font-size: 30px;
        font-weight: 700;
    }
    
    .trans {
        width: 40%;
        height: 38%;
        margin: 15px;
        background-color: white;
        border-radius: 15px;
        display: flex;
        flex-direction: column;
        /* justify-content: center;
        align-items: center; */
    }

    .transhead {
        font-size: 40px;
        font-family: 'Franklin Gothic Medium', 'Arial Narrow', Arial, sans-serif;
        color: #004A90;
        font-weight: 900;
        align-self: center;
        padding-top: 20px;
    }

    .transinhead{
        font-size: 18px;
        padding: 10px;
        font-family: 'Franklin Gothic Medium', 'Arial Narrow', Arial, sans-serif;
        display: flex;
        color: #004A90;
    }

    .transinput {
        padding: 10px;
        margin: 10px;
    }

    .transbutton {
        width: 180px;
        height: 40px;
        align-self: center;
        border: none;
        font-size: larger;
        color: white;
        background-color: #004A90;
        margin-top: 20px;
        border-radius: 15px;
        transition: transform 0.3s ease;
    }

    .transbutton:hover{
        transform: scale(1.2);
        background-color: #080a0a;
        color: white;
    }

    .statement {
        width: 98%;
        height: 45%;
        margin: 15px;
        border-radius: 15px;
        background-color: white;
    }

    .statement-container {
    margin-top: 20px;
}

table {
    width: 100%;
    border-collapse: collapse;
}

th, td {
    text-align: left;
    padding: 8px;
}

th {
    background-color: #004A90;
    color: white;
}

tr:nth-child(even) {
    background-color: #f2f2f2;
}

</style>
</head>
<body>
	<div class="maincont">
		<div class="bar">
            <div class="profile">
                <img class="profileimg" alt="profileimage" src="https://cdn-icons-png.flaticon.com/512/149/149071.png">
                <span>Jubair Ahamed</span>
            </div>
            <hr>
            <div class="options">
                <span>
                    <a href="customer_myaccount.jsp">
                        Account
                    </a>
                </span>
                <span>
                    <a href="customer_transact.jsp">
                        Transact
                    </a>
                </span>
                <span>
                    <a href="statement.jsp">
                        Statement
                    </a>
                </span>
            </div>
            <hr>
                <div class="logout">
                    Logout
                </div>
		</div>

        <!-- /*Right side part developement*/ -->
		<div class="accounts">
            <div class="header">
                <img alt="logo" src="https://cdn.iconscout.com/icon/free/png-512/free-cdn-icon-download-in-svg-png-gif-file-formats--content-delivery-network-global-whcompare-blue-green-web-hosting-pack-communication-icons-1496574.png?f=webp&w=256">
                <span>BANK OF UTOPIA</span>
            </div>
            <form class="trans">
                <span class="transhead">
                    Statement
                </span>
                <span class="transinhead">
                    From Date
                </span>
                <input class="transinput" type="date">
                <span class="transinhead">
                    To Date
                </span>
                <input class="transinput" type="date">
                <button class="transbutton">Get Statement</button>
            </form>
            <div class="statement">
                <div class="statement-container">
                    <table border="1" cellspacing="0" cellpadding="10" width="100%">
                        <thead>
                            <tr>
                                <th>Transaction ID</th>
                                <th>Account</th>
                                <th>Amount</th>
                                <th>Balance</th>
                                <th>Remarks</th>
                            </tr>
                        </thead>
                        <tbody>
                            <!-- JSP code will dynamically insert rows here -->
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
	</div>
</body>
</html>