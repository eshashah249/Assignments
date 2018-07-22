
var req, username, password;
var obj;

function isAsciiOnly(str) {
    for (var k = 0; k < str.length; k++)
        if (str.charCodeAt(k) > 127)
            return false;
    return true;
}

function start()
{
    var userid
    username = document.getElementById("username").value; 
    pwd = document.getElementById("pwd").value;
	
    if(username.length==0)
    {
    	bootbox.alert("Please Enter UserName");
		return 0;
    }
    
    userid = username;
    
    //startServlet("http://photostoreweb-env.rqmwtxmixn.us-east-2.elasticbeanstalk.com/VerifyUser?username="+username+"&pwd="+pwd);
	startServlet("http://localhost:8080/PhotoStoreWeb/VerifyUser?username="+username+"&pwd="+pwd);
    return false;			
}

function startServlet(url)
{	
    req = false; 
    if(window.XMLHttpRequest)
    {
		try
        {
            req = new XMLHttpRequest();
        }
        catch(e)
        {
            req = false;
        }
    }
    else
    {
		if(window.ActiveXObject)
		{
				try
				{
					req = new ActiveXObject();
				}
				catch (e)
				{
					req = false;
				}
		}
    }
    if(req)
    {
		req.onreadystatechange=showContents;
		req.open("GET",url,true);
		req.send(null);
    }
    else
    {
		bootbox.alert("Sorry, XMLHttpRequest cannot be generated");
    }
}

function showContents()
{
    var i;
    	
    if(req.readyState == 4)
    {	
		if (req.status == 200) 
		{
            // If correct then display new UserHomePage
			obj = eval("("+req.responseText+")");
            
            var display;
            if(obj.results.result.length==0)
            {
				display="<p>Username and Password does not match.</p>";
				document.getElementById('invalid-login').innerHTML = display;
            }
            else
            {
				url = 'UserHomePage.html?username=' + encodeURIComponent(username);

				document.location.href = url;
			}	
		}
        else 
        {
            bootbox.alert("There was a problem retrieving the XML data:\n"+ req.status);
        }
    }	
}

function strcmp(a, b)
{   
    return (a<b?-1:(a>b?1:0));  
}

function registeruser()
{
    var userid
    var firstname = document.getElementById("firstname").value;
	var lastname = document.getElementById("lastname").value;
	var newusername = document.getElementById("newusernme").value;
	var email = document.getElementById("email").value;
	var newpwd = document.getElementById("newpwd").value;
    var confnewpwd = document.getElementById("confnewpwd").value; 
    
	if(firstname.length==0)
    {
    	bootbox.alert("Please Enter First Name");
		return 0;
    }
	if(lastname.length==0)
    {
    	bootbox.alert("Please Enter Last Name");
		return 0;
    }
    if(newusername.length==0)
    {
    	bootbox.alert("Please Enter UserName");
		return 0;
    }
	if(email.length==0)
    {
    	bootbox.alert("Please Enter Email Address");
		return 0;
    }
    if(newpwd.length==0)
    {
    	bootbox.alert("Please Enter Password");
		return 0;
    }
	if(strcmp(newpwd, confnewpwd)!== 0)
	{
		bootbox.alert("Password do not match!");
		return 0;
	}
	
    //startServletRegisterUser("http://photostoreweb-env.rqmwtxmixn.us-east-2.elasticbeanstalk.com/RegisterUser?firstname="+encodeURIComponent(firstname)+"&lastname="+encodeURIComponent(lastname)+"&username="+encodeURIComponent(newusername)+"&email="+encodeURIComponent(email)+"&password="+encodeURIComponent(newpwd));
	startServletRegisterUser("http://localhost:8080/PhotoStoreWeb/RegisterUser?firstname="+encodeURIComponent(firstname)+"&lastname="+encodeURIComponent(lastname)+"&username="+encodeURIComponent(newusername)+"&email="+encodeURIComponent(email)+"&password="+encodeURIComponent(newpwd));
    return false;			
}

function startServletRegisterUser(url)
{	
    req = false; 
    if(window.XMLHttpRequest)
    {
		try
        {
            req = new XMLHttpRequest();
        }
        catch(e)
        {
            req = false;
        }
    }
    else
    {
		if(window.ActiveXObject)
		{
				try
				{
					req = new ActiveXObject();
				}
				catch (e)
				{
					req = false;
				}
		}
    }
    if(req)
    {
		req.onreadystatechange=showContentsRegisterUser;
		req.open("GET",url,true);
		req.send(null);
    }
    else
    {
		bootbox.alert("Sorry, XMLHttpRequest cannot be generated");
    }
}

function showContentsRegisterUser()
{
    var i;
    	
    if(req.readyState == 4)
    {	
		if (req.status == 200) 
		{
            // If correct then display new UserHomePage
			obj = eval("("+req.responseText+")");
            
            var display;
            if(obj.results.result.length==0)
            {
				display="<p class=\"redtext\">Error occurred while registering user.</p>";
				document.getElementById('register-user-display').innerHTML = display;
            }
            else
            {
				display="<p class=\"greentext\">User Registered Successfully!</p>";
				document.getElementById('register-user-display').innerHTML = display;
			}	
		}
        else 
        {
            bootbox.alert("There was a problem retrieving the XML data:\n"+ req.status);
        }
    }	
}