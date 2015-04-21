<div id="datasetlinks" class="Section1 section">
	<h4 class="alert alert-info">Data Set Link:</h4>
	<ul>
		<li class="Section2"><label for="URL" style="width: 200">URL:
		</label> <form:input path="field_dataset_url" name="URL" /></li>
		<li class="Section2"><label for="Label" style="width: 200">Label:
		</label> <form:input path="field_dataset_label" name="Label" /></li>
		<li class="Section2">Link Note (Optional instructions or
			remarks):<br> <form:textarea path="field_dataset_link_note"
				name="Link_Note" cols="80" rows="3" />
		</li>
	</ul>
	<div><label for="Userfile0"> Attach
		Files:</label></div>
	<div>

		<form:input path="field_userfile0" id="field_userfile0"
			onchange="displayFile('field_userfile0','Userfile0')" type="file" />
		<form:input path="field_userfile0" id="Userfile0" type="text"
			size="90" style="background:#D3D3D3;" onfocus="blur()" />
	</div>
	<div id="divUserfile1">
		<form:input path="field_userfile1" id="field_userfile1" type="file"
			onchange="displayFile('field_userfile1','Userfile1')" />
		<form:input path="field_userfile1" size="90"
			style="background:#D3D3D3;" id="Userfile1" type="text"
			onfocus="blur()" />
		<br>
	</div>
	<div id="divUserfile2">
		<form:input path="field_userfile2" id="field_userfile2" type="file"
			onchange="displayFile('field_userfile2','Userfile2')" />
		<form:input path="field_userfile2" size="90"
			style="background:#D3D3D3;" id="Userfile2" type="text"
			onfocus="blur()" />
		<br>
	</div>
	<div id="divUserfile3">
		<form:input path="field_userfile3" id="field_userfile3" type="file"
			onchange="displayFile('field_userfile3','Userfile3')" />
		<form:input path="field_userfile3" size="90"
			style="background:#D3D3D3;" id="Userfile3" type="text"
			onfocus="blur()" />
		<br>
	</div>

</div>