function deleteImages(){
	alert("Started");
	var selectedInputs = $("input:checked");
	var selectedIds = [];
	selectedInputs.each(function(){
		selectedIds.push($(this).attr('id'));
	});
	if (selectedIds.length < 1){
		alert("No images selected!");
	}else{
		$.post("/delete",
				{names:selectedIds},
				function(response){
					var errors = $('<ul>');
					$.each(response,function(){
						if("ok" == this.status){
							var element = document.getElementById(this.name);
							$(element).parent().remove();
						}else{
							errors.append($('<li>',{html: "Failed to remove "+this.name+": "+this.status}));
						}
					});
					if(errors.length > 0){
						$('#error').empty().append(errors);
					}
					
				},
				"json");
	}
	
}
$(document).ready(function(){

	$("#delete").click(deleteImages);
	alert("bbb");
});