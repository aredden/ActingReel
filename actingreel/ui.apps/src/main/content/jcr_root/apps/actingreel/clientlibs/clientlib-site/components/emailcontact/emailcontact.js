$(document).ready(function(){
	$('#submitEmailContact').click(function(){
		var info = {
				email: $('#EmailContactEmail').val(),
				title: $('#EmailContactTextArea').val(),
				message: $('#EmailContactTitle').val()
		}
		
		
		$.ajax({
			type: 'POST',
			url: '/bin/actingreel/documents',
			contentType: 'application/json',
			data: JSON.stringify(info),
			success: function(msg){
				var json = jQuery.parseJSON(msg);
				alert(json);
				console.log("success");
			},
			error: function(xhr,status,err){
				console.log(xhr.statusText);
				console.log(status);
				console.log(err);
			}
		})
	})
})