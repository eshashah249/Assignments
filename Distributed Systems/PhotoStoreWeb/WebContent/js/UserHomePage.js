$.fn.checkedListBoxfunction = function () {
    $('.list-group.checked-list-box .list-group-item').each(function () {
        
        // Settings
        var $widget = $(this),
            $checkbox = $('<input type="checkbox" class="hidden" />'),
            color = ($widget.data('color') ? $widget.data('color') : "primary"),
            style = ($widget.data('style') == "button" ? "btn-" : "list-group-item-"),
            settings = {
                on: {
                    icon: 'glyphicon glyphicon-check'
                },
                off: {
                    icon: 'glyphicon glyphicon-unchecked'
                }
            };
            
        $widget.css('cursor', 'pointer')
        $widget.append($checkbox);

        // Event Handlers
        $widget.on('click', function () {
            $checkbox.prop('checked', !$checkbox.is(':checked'));
            $checkbox.triggerHandler('change');
            updateDisplay();
        });
        $checkbox.on('change', function () {
            updateDisplay();
        });
          

        // Actions
        function updateDisplay() {
            var isChecked = $checkbox.is(':checked');

            // Set the button's state
            $widget.data('state', (isChecked) ? "on" : "off");

            // Set the button's icon
            $widget.find('.state-icon')
                .removeClass()
                .addClass('state-icon ' + settings[$widget.data('state')].icon);

            // Update the button's color
            if (isChecked) {
                $widget.addClass(style + color + ' active');
            } else {
                $widget.removeClass(style + color + ' active');
            }
        }

        // Initialization
        function init() {
            
            if ($widget.data('checked') == true) {
                $checkbox.prop('checked', !$checkbox.is(':checked'));
            }
            
            updateDisplay();

            // Inject the icon if applicable
            if ($widget.find('.state-icon').length == 0) {
                $widget.prepend('<span class="state-icon ' + settings[$widget.data('state')].icon + '"></span>');
            }
        }
        init();
    });
    
    $('#get-checked-data').on('click', function(event) {
        event.preventDefault(); 
        var checkedItems = {}, counter = 0;
        $("#check-list-box li.active").each(function(idx, li) {
            checkedItems[counter] = $(li).text();
            counter++;
        });
        $('#display-json').html(JSON.stringify(checkedItems, null, '\t'));
    });
};

var req, username, password;
var obj;
var selectedItems;
var selectedUris;

$('#get-checked-data-download').on('click', function(event) {
        event.preventDefault(); 
        var checkedItems = { 'FileNames' : [] }, counter = 0;
		selectedUris = [];
        $("#check-list-box li.active").each(function(idx, li) {
            checkedItems.FileNames.push($(li).text());
			selectedUris.push($(li).text());
            counter++;
        });
		selectedItems = JSON.stringify(checkedItems, null, '\t');
		
		//displayJson();
		
		downloadData();
        
});

$('#get-checked-data-delete').on('click', function(event) {
        var checkedItems = { 'FileNames' : [] }, counter = 0;
        $("#check-list-box li.active").each(function(idx, li) {
            checkedItems.FileNames.push($(li).text());
            counter++;
        });
		selectedItems = JSON.stringify(checkedItems, null, '\t');
		
		//displayJson();
		
		deleteData();
});
			
function displayJson(){
	document.getElementById("display-json").innerHTML = selectedItems;	
}

function isAsciiOnly(str) {
    for (var k = 0; k < str.length; k++)
        if (str.charCodeAt(k) > 127)
            return false;
    return true;
}

function autoHideAlert(str, timeOut) {
    var dialog;
	setTimeout(function(){
        dialog = bootbox.dialog({
					message: '<p class="text-center">'+str+'</p>',
					closeButton: false
					});
		// do something in the background
		dialog.modal('hide');
    }, timeOut);
	
}

function getUrlVars() {
    var vars = {};
    var parts = window.location.href.replace(/[?&]+([^=&]+)=([^&]*)/gi, function(m,key,value) {
        vars[key] = value;
    });
    return vars;
}

$('.preview').popover({
    'trigger':'hover',
    'html':true,
    'content':function(){
        return "<img src='"+$(this).data('imageUrl')+"'>";
    }
});

function getUserData()
{
    var userid
	
	var url = document.location.href,
        params = url.split('?')[1].split('&'),
        data = {}, tmp;
    for (var i = 0, l = params.length; i < l; i++) {
         tmp = params[i].split('=');
         data[tmp[0]] = tmp[1];
    }
	
	username = decodeURIComponent(data.username);  
    password = "pssword";
	
    if(username.length==0)
    {
    	bootbox.alert("Username is not passed");
		return 0;
    }
    
    userid = username;
    
    //startServlet("http://photostoreweb-env.rqmwtxmixn.us-east-2.elasticbeanstalk.com/GetUserData?username="+username);
	startServlet("http://localhost:8080/PhotoStoreWeb/GetUserData?username="+username);
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
    
	document.getElementById("ajax-loader-display").innerHTML ="<img src=\"img/ajax-loader.gif\">";
	document.getElementById('display-username').innerHTML = username;
	$("#check-list-box").empty();
	
    if(req.readyState == 4)
    {	
		if (req.status == 200) 
		{
            obj = eval("("+req.responseText+")");
            
			document.getElementById("ajax-loader-display").innerHTML ="";
			
            var display;
            if(obj.results.result.length==0)
            {
                display="<br />";
            }
            else
            {	
				for (i=0;i<obj.results.result.length;i++)
				{
					FILENAME = unescape(decodeURI(obj.results.result[i].filename));
					FILENAME = FILENAME.replace(/\+/g," ");
					
					FILEURL = unescape(decodeURI(obj.results.result[i].fileurl));

					display+="";
					
					var fileData = "<div class=\"row\"><div class=\"col-lg-6\"><li class='list-group-item'>"+FILENAME+"</li></div><div class=\"col-lg-6\"><img src=\""+FILEURL + "\" class=\"img-rounded\" alt=\"Cinque Terre\" width=\"304\" height=\"236\"></div></div><br />"
					
					$("#check-list-box").append(fileData);
				}
			}
			
			$.fn.checkedListBoxfunction();
			
		}
        else 
        {
            bootbox.alert("There was a problem retrieving the XML data:\n"+ req.status);
        }
    }	
}

function downloadURI(uri, name) {
  var link = document.createElement("a");
  link.download = name;
  link.href = uri;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  delete link;
}

function downloadData(){
	
	if(username.length==0)
    {
    	bootbox.alert("Username is not passed");
		return 0;
    }
    
	for (i = 0; i < selectedUris.length; i++) { 
		downloadURI("https://s3.us-east-2.amazonaws.com/photostoragedistributedsystems-us/"+username+"/"+selectedUris[i], "")
	}
	
	bootbox.alert("Download Successful!");
	
	//startDownloadServlet("http://localhost:8080/PhotoStoreWeb/DownloadData?username="+encodeURIComponent(username)+"&filenames="+encodeURIComponent(selectedItems));
	//startDownloadServlet("http://photostoreweb-env.rqmwtxmixn.us-east-2.elasticbeanstalk.com/DownloadData?username="+encodeURIComponent(username)+"&filenames="+encodeURIComponent(selectedItems));
    return false;
}

function startDownloadServlet(url)
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
		req.onreadystatechange=showDownloadContents;
		req.open("GET",url,true);
		req.send(null);
    }
    else
    {
		bootbox.alert("Sorry, XMLHttpRequest cannot be generated");
    }
}

function showDownloadContents()
{
    var i;
    	
    if(req.readyState == 4)
    {	
		if (req.status == 200) 
		{
            obj = eval("("+req.responseText+")");
            
            var display;
            if(obj.results.result.length==0)
            {
                bootbox.alert("Download Unsuccessful:\n"+ req.status);
            }
            else
            {
                //autoHideAlert("Download Successful!", 2500);
				bootbox.alert("Download Successful!");
			}
		}
        else 
        {
            bootbox.alert("There was a problem retrieving the XML data:\n"+ req.status);
        }
    }	
}

function deleteData(){
	
	if(username.length==0)
    {
    	bootbox.alert("Username is not passed");
		return 0;
    }
    
    //startDeleteServlet("http://photostoreweb-env.rqmwtxmixn.us-east-2.elasticbeanstalk.com/DeleteData?username="+encodeURIComponent(username)+"&filenames="+encodeURIComponent(selectedItems));
	startDeleteServlet("http://localhost:8080/PhotoStoreWeb/DeleteData?username="+encodeURIComponent(username)+"&filenames="+encodeURIComponent(selectedItems));
    return false;
}

function startDeleteServlet(url)
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
		req.onreadystatechange=showDeleteContents;
		req.open("GET",url,true);
		req.send(null);
    }
    else
    {
		bootbox.alert("Sorry, XMLHttpRequest cannot be generated");
    }
}

function showDeleteContents()
{
    var i;
    
	document.getElementById("ajax-loader-display").innerHTML ="<img src=\"img/ajax-loader.gif\">";
	
    if(req.readyState == 4)
    {	
		if (req.status == 200) 
		{
            document.getElementById("ajax-loader-display").innerHTML ="";
			
			obj = eval("("+req.responseText+")");
            
            var display;
            if(obj.results.result.length==0)
            {
                bootbox.alert("Delete Unsuccessful:\n"+ req.status);
            }
            else
            {
                bootbox.alert("Delete Successful!");
				
				getUserData();
			}
		}
        else 
        {
            bootbox.alert("There was a problem retrieving the XML data:\n"+ req.status);
        }
    }	
}

function uploadData(){
    
	var uploadfile = document.getElementById('upload-file').files;
	var toUploadItems = { 'FileNames' : [] };
	
	for (var i = 0; i < uploadfile.length; i++){
		toUploadItems.FileNames.push("C:\\PhotoStore\\" + uploadfile[i].name);
	}
    
	
	jsonUploadItems = JSON.stringify(toUploadItems, null, '\t');
	
	//bootbox.alert(jsonUploadItems);
	
	if(username.length==0)
    {
    	bootbox.alert("Username is not passed");
		return 0;
    }
    
    //startUploadServlet("http://photostoreweb-env.rqmwtxmixn.us-east-2.elasticbeanstalk.com/UploadData?username="+encodeURIComponent(username)+"&filenames="+encodeURIComponent(jsonUploadItems));
	startUploadServlet("http://localhost:8080/PhotoStoreWeb/UploadData?username="+encodeURIComponent(username)+"&filenames="+encodeURIComponent(jsonUploadItems));
    return false;
}

function startUploadServlet(url)
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
		req.onreadystatechange=showUploadContents;
		req.open("GET",url,true);
		req.send(null);
    }
    else
    {
		bootbox.alert("Sorry, XMLHttpRequest cannot be generated");
    }
}

function showUploadContents()
{
    var i;
    
	document.getElementById("ajax-loader-display").innerHTML ="<img src=\"img/ajax-loader.gif\">";	
    
	if(req.readyState == 4)
    {	
		if (req.status == 200) 
		{
            document.getElementById("ajax-loader-display").innerHTML ="";
			
			obj = eval("("+req.responseText+")");
            
            var display;
            if(obj.results.result.length==0)
            {
                bootbox.alert("Upload Unsuccessful:\n"+ req.status);
            }
            else
            {
                bootbox.alert("Upload Successful!");
				
				getUserData();
			}
		}
        else 
        {
            bootbox.alert("There was a problem retrieving the XML data:\n"+ req.status);
        }
    }	
}