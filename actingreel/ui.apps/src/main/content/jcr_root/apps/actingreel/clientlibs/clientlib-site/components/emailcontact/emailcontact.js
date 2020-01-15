$(document).ready(function(){
	$('#submitEmailContact').click(function(event){
		
		event.preventDefault();
		
		var info = {
				email: $('#EmailContactEmail').val(),
				title: $('#EmailContactTitle').val(),
				message: $('#EmailContactTextArea').val(),
				id: event.target.parentNode.id
		}
		
		var stringdata = JSON.stringify(info);
		
		var successfunc = function(msg){
			console.log("success");
		}
		
		var errorfunc = function(xhr,status,err){
			console.log(xhr.statusText);
			console.log(xhr.responseText);
			console.log(status);
			console.log(err);
		}
		
		$.ajax( {
			type: 'POST',
			url: '/bin/actingreel/documents',
			contentType: '"application/json; charset=utf-8"',
			data: stringdata,
			success: successfunc,
			error: errorfunc
		})
	})
})