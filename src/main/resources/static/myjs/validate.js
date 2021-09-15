$(document).ready(function(){
	$("#empNameError").hide();
    $("#empName").change(function(){
		var val = $("#empName").val();
		$.ajax({
			url : 'validate',
			data:{ 'ename': val},
			success:function(resp) {
				if(resp!='') {
					$("#empNameError").show();
					$("#empNameError").html(resp);
					$("#empNameError").css('color','red');
				} else {
					$("#empNameError").hide();
				}
			}
		})
	});		
});