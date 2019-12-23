$(document).ready(function(){
	$('#submitEmailContact').click(function(){

		var email = $('#EmailContactInput').val();
		var message = $('#EmailContactTextArea').val();
		$.ajax({
			type: 'POST',
			url: '/bin/actingreel/documents.servlet',
			data: 'email='+email+'&message='+message,
			success: function(msg){
				var json = jQuery.parseJSON(msg);
				alert(json);
				$('#EmailContactResponse').val(json);
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