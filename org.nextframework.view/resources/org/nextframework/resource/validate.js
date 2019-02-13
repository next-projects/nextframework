//----------------------------------------------------------------------

	//Lista com todas as propriedades.. para evitar um loop em todas as propriedades para cada validação
	var formProperties = null;
	
	function organizeProperties(form){
		formProperties = new Array();
		for (var j = 0; j < form.elements.length; j+=1){
			element = form.elements[j];
			if(element.name == null || element.name == '') {continue;}
			if(formProperties[element.name]){
				if(formProperties[element.name].isArray){
					//já é lista			
					var obj = formProperties[element.name];
					obj[obj.length] = element;
				} else {
					//criar lista
					var obj = new Array(formProperties[element.name], element);
					obj.isArray = true;
					formProperties[element.name] = obj;
				}
			} else {
				formProperties[element.name] = element;
			}
	
		}
	}

//------------------------------------------------------------

	//function to add indexedProperties support
	function formIndexedProperties(formfunction, identifier, property, message, func, indexedList, form){
		var index = 0;
		for (var j = 0; j < form.elements.length; j++){
			element = form.elements[j];
			if(element.name == null) {
				continue;
			}
			var mat = false;
			//element.name.match("^"+indexedList+"\\[(\\d*)\\]."+property+"$")
			
			try {
				var inicio = element.name.substring(0, indexedList.length + 1);
				var meio = element.name.substring(indexedList.length + 1, element.name.length - property.length - 2);
				var fim = element.name.substring(element.name.length - property.length - 2, element.name.length);
				mat = (inicio == indexedList + '[') && (fim == '].'+property) && isAllDigits(meio);
			}catch(e){
				/*catar um index out of bounds que possa acontecer e passar sem dar erro*/ 
				continue;
			}
			//alert(element.name+"  "+ indexedList+"\\[(\\d*)\\]."+property +"  "+element.name.match("^"+indexedList+"\\[(\\d*)\\]."+property+"$"));
			if(mat && element.name != '') {
				//alert('n=' + element.name + ';\nm=' + mat + ';');
				//if (mat == element.name+',0'){
					//alert('OK');
					eval('formfunction.'+identifier+index+' = new Array(element.name, message, func)');
					index++;
				//}
			}
		}
	}

	function invalidFields(form, fields, msgs, validationName){
		alert(msgs.join('\n'));
		hasFocus = false;
		/*
		for(i = 0; i < fields.length; i++){
			try{
				fields[i].style.backgroundColor = '#EEFFFF';
				fields[i].style.borderWidth = '1px';
				if(!hasFocus){
					fields[i].focus();
					hasFocus = true;
				}
			}catch(exception){}
		}
		*/
	}

	// Retorna uma string apenas com os numeros da string enviada
	function ApenasNum(strParm) {
		strParm = String(strParm);
		var chrPrt = "0";
		var strRet = "";
		var j = 0;
		for ( var i = 0; i < strParm.length; i++) {
			chrPrt = strParm.substring(i, i + 1);
			if (chrPrt.match(/\d/)) {
				if (j == 0) {
					strRet = chrPrt;
					j = 1;
				} else {
					strRet = strRet.concat(chrPrt);
				}
			}
		}
		return strRet;
	}

//----------------------------------------------------------------------

	/**
	* A field is considered valid if greater than the specified minimum.
	* Fields are not checked if they are disabled.
	* <p>
	* <strong>Caution:</strong> Using <code>validateMinLength</code> on a password field in a 
	*  login page gives unnecessary information away to hackers. While it only slightly
	*  weakens security, we suggest using it only when modifying a password.</p>
	* @param form The form validation is taking place on.
	*/
	function validateMinLength(form) {
		var isValid = true;
		var focusField = null;
		var i = 0;
		var fields = new Array();
		var formName = form.getAttributeNode("name");


		oMinLength = eval('new ' + formName.value + '_minlength()');

		for (x in oMinLength) {
			// form[] n?o funciona direito quando o nome
			// dos elements mudam dinamicamente
			//var field = form[oRequired[x][0]];

			var field = null;
			field = formProperties[oMinLength[x][0]];
			/*
			for (var j = 0; j < form.elements.length; j++){
				element = form.elements[j];
				if(element.name == null) continue;
				if(element.name == oMinLength[x][0]){
					field = element;
				}
			}
			*/
			if ((field.type == 'hidden' ||
				field.type == 'text' ||
				field.type == 'password' ||
				field.type == 'textarea') &&
				field.disabled == false) {

				var iMin = parseInt(oMinLength[x][2]("minlength"));
				if ((trim(field.value).length > 0) && (field.value.length < iMin)) {
					if (i == 0) {
						focusField = field;
					}
					fields[i++] = oMinLength[x][1];
					isValid = false;
				}
			}
		}
		if (fields.length > 0) {
		   try{focusField.focus();}catch(e){}
		   alert(fields.join('\n'));
		}
		return isValid;
	}

//----------------------------------------------------------------------

	/**
	* A field is considered valid if less than the specified maximum.
	* Fields are not checked if they are disabled.
	* <p>
	* <strong>Caution:</strong> Using <code>validateMaxLength</code> on a password field in a 
	*  login page gives unnecessary information away to hackers. While it only slightly
	*  weakens security, we suggest using it only when modifying a password.</p>
	* @param form The form validation is taking place on.
	*/
	function validateMaxLength(form) {
		var isValid = true;
		var focusField = null;
		var i = 0;
		var fields = new Array();
		var fieldObjs = new Array();
		var formName = form.getAttributeNode("name"); 

		oMaxLength = eval('new ' + formName.value + '_maxlength()');		
		for (x in oMaxLength) {
			// form[] n?o funciona direito quando o nome
			// dos elements mudam dinamicamente
			//var field = form[oRequired[x][0]];
			
			var field = null;
			field = formProperties[oMaxLength[x][0]];
			/*
			for (var j = 0; j < form.elements.length; j++){
				element = form.elements[j];
				if(element.name == null) continue;
				if(element.name == oMaxLength[x][0]){
					field = element;
				}
			}
			*/

			if ((field.type == 'hidden' ||
				field.type == 'text' ||
				field.type == 'password' ||
				field.type == 'textarea') &&
				field.disabled == false) {

				var iMax = parseInt(oMaxLength[x][2]("maxlength"));
				if (field.value.length > iMax) {
					if (i == 0) {
						focusField = field;
					}
					fields[i++] = oMaxLength[x][1];
					fieldObjs[i] = form[oMaxLength[x][0]];
					isValid = false;
				}
			}
		}
		if (fields.length > 0) {
			invalidFields(form, fieldObjs, fields, 'required');
		}
		return isValid;
	}
	
//----------------------------------------------------------------------

	/**
	* Check to see if fields is in a valid integer range.
	* Fields are not checked if they are disabled.
	* <p>
	* @param form The form validation is taking place on.
	*/
	function validateIntRange(form) {
		var isValid = true;
		var focusField = null;
		var i = 0;
		var fields = new Array();
		var formName = form.getAttributeNode("name"); 

		oRange = eval('new ' + formName.value + '_intRange()');		
		for (x in oRange) {
			// form[] n?o funciona direito quando o nome
			// dos elements mudam dinamicamente
			//var field = form[oRequired[x][0]];

			var field = null;
			field = formProperties[oRange[x][0]];
			/*
			for (var j = 0; j < form.elements.length; j++){
				element = form.elements[j];
				if(element.name == null) continue;
				if(element.name == oRange[x][0]){
					field = element;
				}
			}*/
			
			if (field.disabled == false)  {
				var value = '';
				if (field.type == 'hidden' ||
					field.type == 'text' || field.type == 'textarea' ||
					field.type == 'radio' ) {
					value = field.value;
				}
				if (field.type == 'select-one') {
					var si = field.selectedIndex;
					if (si >= 0) {
						value = field.options[si].value;
					}
				}
				if (value.length > 0) {
					var iMin = parseInt(oRange[x][2]("min"));
					var iMax = parseInt(oRange[x][2]("max"));
					var iValue = parseInt(value);
					if (!(iValue >= iMin && iValue <= iMax)) {
						if (i == 0) {
							focusField = field;
						}
						fields[i++] = oRange[x][1];
						isValid = false;
					}
				}
				
				var iMax = parseInt(oRange[x][2]("length"));
				if (field.value.length != iMax) {
					if (i == 0) {
						focusField = field;
					}
					fields[i++] = oRange[x][1];
					isValid = false;
				}
			}
		}
		if (fields.length > 0) {
			try{focusField.focus();}catch(e){}
			alert(fields.join('\n'));
		}
		return isValid;
	}
	
	function validateYear(form) {
		var isValid = true;
		var focusField = null;
		var i = 0;
		var fields = new Array();
		var formName = form.getAttributeNode("name"); 

		oRange = eval('new ' + formName.value + '_year()');		
		for (x in oRange) {
			// form[] n?o funciona direito quando o nome
			// dos elements mudam dinamicamente
			//var field = form[oRequired[x][0]];

			var field = null;
			field = formProperties[oRange[x][0]];
			/*
			for (var j = 0; j < form.elements.length; j++){
				element = form.elements[j];
				if(element.name == null) continue;
				if(element.name == oRange[x][0]){
					field = element;
				}
			}*/
			
			if (field.disabled == false)  {
				var value = '';
				if (field.type == 'hidden' ||
					field.type == 'text' || field.type == 'textarea' ||
					field.type == 'radio' ) {
					value = field.value;
				}
				if (field.type == 'select-one') {
					var si = field.selectedIndex;
					if (si >= 0) {
						value = field.options[si].value;
					}
				}
				if (value.length > 0) {
					var iMin = parseInt(oRange[x][2]("min"));
					var iMax = parseInt(oRange[x][2]("max"));
					var iValue = parseInt(value);
					if (!(iValue >= iMin && iValue <= iMax)) {
						if (i == 0) {
							focusField = field;
						}
						fields[i++] = oRange[x][1];
						isValid = false;
					}
				}
			}
		}
		if (fields.length > 0) {
			try{focusField.focus();}catch(e){}
			alert(fields.join('\n'));
		}
		return isValid;
	}
	
//----------------------------------------------------------------------

	/**
	* Check to see if fields are a valid number.
	* Fields are not checked if they are disabled.
	*/
	function validateAnyNumber(form, nomeTipo, limitValue) {
		var bValid = true;
		var focusField = null;
		var i = 0;
		var fields = new Array();
		var fieldObjs = new Array();
		var formName = form.getAttributeNode("name"); 

		oNumber = eval('new ' + formName.value + '_' + nomeTipo + 'Validations()');
		for (x in oNumber) {
			// form[] nao funciona direito quando o nome dos elements mudam dinamicamente
			//var field = form[oRequired[x][0]];

			var field = null;
			field = formProperties[oNumber[x][0]];
			/*
			for (var j = 0; j < form.elements.length; j++){
				element = form.elements[j];
				if(element.name == null) continue;
				if(element.name == oNumber[x][0]){
					field = element;
				}
			}
			*/
			if ((field.type == 'hidden' ||
				field.type == 'text' ||
				field.type == 'textarea' ||
				field.type == 'select-one' ||
				field.type == 'radio') &&
				field.disabled == false) {

				var value = '';
				// get field's value
				if (field.type == "select-one") {
					var si = field.selectedIndex;
					if (si >= 0) {
						value = field.options[si].value;
					}
				} else {
					value = field.value;
				}

				if (value.length > 0) {

					if (!isAllDigits(value)) {
						bValid = false;
						if (i == 0) {
							focusField = field;
						}
						fields[i++] = oNumber[x][1];
						fieldObjs[i] = form[oNumber[x][0]];

					} else {
						var iValue = parseInt(value);
						if (isNaN(iValue) || !(iValue >= (limitValue*-1) && iValue <= limitValue)) {
							if (i == 0) {
								focusField = field;
							}
							fields[i++] = oNumber[x][1];
							fieldObjs[i] = form[oNumber[x][0]];
							bValid = false;
						}
					}
				}
			}
		}
		if (fields.length > 0) {
			invalidFields(form, fieldObjs, fields, nomeTipo);
		}
		return bValid;
	}

	function isAllDigits(argvalue) {
		argvalue = argvalue.toString();
		var validChars = "0123456789";
		var startFrom = 0;
		if (argvalue.substring(0, 2) == "0x") {
			validChars = "0123456789abcdefABCDEF";
			startFrom = 2;
		}
		
		//Este código dá erro ao processar os números 08,8 ou 1,08.
		//else if (argvalue.charAt(0) == "0") {
		//	validChars = "01234567";
		//	startFrom = 1;
		//}
		else if (argvalue.charAt(0) == "-") {
			startFrom = 1;
		}

		for (var n = startFrom; n < argvalue.length; n++) {
			if (validChars.indexOf(argvalue.substring(n, n+1)) == -1) return false;
		}
		return true;
	}
	
	
// ----------------------------------------------------------------------

	/**
	* Check to see if fields are a valid short.
	* Fields are not checked if they are disabled.
	* <p>
	* @param form The form validation is taking place on.
	*/
	function validateShort(form) {
		return validateAnyNumber(form, 'Short', 32767);
	}
	
// ----------------------------------------------------------------------
	
	/**
	 * Check to see if fields are a valid integer.
	 * Fields are not checked if they are disabled.
	 * <p>
	 * @param form The form validation is taking place on.
	 */
	function validateInteger(form) {
		return validateAnyNumber(form, 'Integer', 2147483647);
	}
	
//----------------------------------------------------------------------
	
	/**
	 * Check to see if fields are a valid long.
	 * Fields are not checked if they are disabled.
	 * <p>
	 * @param form The form validation is taking place on.
	 */
	function validateLong(form) {
		return validateAnyNumber(form, 'Long', 9223372036854775807);
	}
	
//----------------------------------------------------------------------

	/**
	* Check to see if fields are a valid float.
	* Fields are not checked if they are disabled.
	* <p>
	* @param form The form validation is taking place on.
	*/
	function validateFloat(form) {

		var bValid = true;
		var focusField = null;
		var i = 0;
		var fields = new Array();
		var fieldObjs = new Array();
		var formName = form.getAttributeNode("name");

			
		oFloat = eval('new ' + formName.value + '_FloatValidations()');
		for (x in oFloat) {
			// form[] n?o funciona direito quando o nome
			// dos elements mudam dinamicamente
			//var field = form[oRequired[x][0]];

			var field = null;
			field = formProperties[oFloat[x][0]];
			/*
			for (var j = 0; j < form.elements.length; j++){
				element = form.elements[j];
				if(element.name == null) continue;
				if(element.name == oFloat[x][0]){
					field = element;
				}
			}
			*/
			
			if ((field.type == 'hidden' ||
				field.type == 'text' ||
				field.type == 'textarea' ||
				field.type == 'select-one' ||
				field.type == 'radio') &&
				field.disabled == false) {
		
				var value = '';
				// get field's value
				if (field.type == "select-one") {
					var si = field.selectedIndex;
					if (si >= 0) {
						value = field.options[si].value;
					}
				} else {
					value = field.value;
				}
				if (value.length > 0) {
					while(value.indexOf('.') >= 0){
						value = value.replace('.', "");
					}
					//value = value.replaceAll(".", "");
					//alert('>'+value);
					// remove ',' before checking digits
					var tempArray = value.split(',');
					//alert(value);
					//Strip off leading '0'
					if(tempArray.length <= 2){
						//alert(tempArray[0]);
						//alert(tempArray[0].match(/\d*/));
						if(!isAllDigits(tempArray[0])){
							if (i == 0) {
								focusField = field;
							}
							fields[i++] = oFloat[x][1];
							fieldObjs[i] = form[oFloat[x][0]];
							bValid = false;
							continue;
						} 
						//alert(tempArray[1]);
						if(tempArray.length==2 && !isAllDigits(tempArray[1])){
							if (i == 0) {
								focusField = field;
							}
							fields[i++] = oFloat[x][1];
							fieldObjs[i] = form[oFloat[x][0]];
							bValid = false;
							continue;							
						}
					} else {
							//alert(tempArray.length);
							if (i == 0) {
								focusField = field;
							}
							fields[i++] = oFloat[x][1];
							fieldObjs[i] = form[oFloat[x][0]];
							bValid = false;
							continue;					
					}
					/*
					var zeroIndex = 0;
					var joinedString= tempArray.join('');
					while (joinedString.charAt(zeroIndex) == '0') {
						zeroIndex++;
					}
					var noZeroString = joinedString.substring(zeroIndex,joinedString.length);

					if (!isAllDigits(noZeroString)) {
						bValid = false;
						if (i == 0) {
							focusField = field;
						}
						fields[i++] = oFloat[x][1];

					} else {
					var iValue = parseFloat(value);
					if (isNaN(iValue)) {
						if (i == 0) {
							focusField = field;
						}
						fields[i++] = oFloat[x][1];
						bValid = false;
					}
					}
					*/
				}
			}
		}
		if (fields.length > 0) {
			invalidFields(form, fieldObjs, fields, 'float');
		}
		return bValid;
	}
	
//----------------------------------------------------------------------

	/**
	*  Verifica determinado campo ? um inscricaoEstadual v?lido
	* <p>
	* @param form The form validation is taking place on.
	*/
	function validateInscricaoEstadual(form) {
		var isValid = true;
		var focusField = null;
		var i = 0;
		var fields = new Array();
		var fieldObjs = new Array();
		var formName = form.getAttributeNode("name");

		oInscricaoEstadual = eval('new ' + formName.value + '_inscricaoEstadual()');

		for (x in oInscricaoEstadual) {
			// form[] n?o funciona direito quando o nome
			// dos elements mudam dinamicamente
			//var field = form[oInscricaoEstadual[x][0]];

			var field = null;
			field = formProperties[oInscricaoEstadual[x][0]];
			/*
			for (var j = 0; j < form.elements.length; j++){
				element = form.elements[j];
				if(element.name == null) continue;
				if(element.name == oInscricaoEstadual[x][0]){
					field = element;
				}
			}
			*/
			
			if ((field.type == 'hidden' ||
				field.type == 'text' ||
				field.type == 'password' ||
				field.type == 'textarea') &&
				field.disabled == false) {
				
				if(field.value.replace( /\s*/, "" ).length==0) return true;
				
				if (ApenasNum(field.value).length != 14) {
					if (i == 0) {
						focusField = field;
					}
					fields[i++] = oInscricaoEstadual[x][1];
					isValid = false;
				}
			}
		}
		if (fields.length > 0) {
			invalidFields(form, fieldObjs, fields, 'inscricaoEstadual');
		}
		return isValid;
	}
	
//----------------------------------------------------------------------

	/**
	* Check to see if fields are in a valid float range.
	* Fields are not checked if they are disabled.
	* <p>
	* @param form The form validation is taking place on.
	*/
	function validateFloatRange(form) {
		var isValid = true;
		var focusField = null;
		var i = 0;
		var fields = new Array();
		var formName = form.getAttributeNode("name"); 

		oRange = eval('new ' + formName.value + '_floatRange()');
		for (x in oRange) {
			// form[] n?o funciona direito quando o nome
			// dos elements mudam dinamicamente
			//var field = form[oRequired[x][0]];

			var field = null;
			field = formProperties[oRange[x][0]];
			/*
			for (var j = 0; j < form.elements.length; j++){
				element = form.elements[j];
				if(element.name == null) continue;
				if(element.name == oRange[x][0]){
					field = element;
				}
			}
			*/
			
			if ((field.type == 'hidden' ||
				field.type == 'text' || field.type == 'textarea') &&
				(field.value.length > 0)  &&
				 field.disabled == false) {
		
				var fMin = parseFloat(oRange[x][2]("min"));
				var fMax = parseFloat(oRange[x][2]("max"));
				var fValue = parseFloat(field.value);
				if (!(fValue >= fMin && fValue <= fMax)) {
					if (i == 0) {
						focusField = field;
					}
					fields[i++] = oRange[x][1];
					isValid = false;
				}
			}
		}
		if (fields.length > 0) {
			try{focusField.focus();}catch(e){}
			alert(fields.join('\n'));
		}
		return isValid;
	}
	
//----------------------------------------------------------------------

	/**
	* Check to see if fields are in a valid float range.
	* Fields are not checked if they are disabled.
	* <p>
	* @param form The form validation is taking place on.
	*/
	function validateFloatMinValue(form) {
		var isValid = true;
		var focusField = null;
		var i = 0;
		var fields = new Array();
		var formName = form.getAttributeNode("name"); 

		oRange = eval('new ' + formName.value + '_floatMinValue()');
		for (x in oRange) {
			// form[] n?o funciona direito quando o nome
			// dos elements mudam dinamicamente
			//var field = form[oRequired[x][0]];

			var field = null;
			field = formProperties[oRange[x][0]];
			/*
			for (var j = 0; j < form.elements.length; j++){
				element = form.elements[j];
				if(element.name == null) continue;
				if(element.name == oRange[x][0]){
					field = element;
				}
			}
			*/
			if ((field.type == 'hidden' ||
				field.type == 'text' || field.type == 'textarea') &&
				(field.value.length > 0)  &&
				 field.disabled == false) {
		
				var fMin = parseFloat(oRange[x][2]("min"));
				//var fMax = parseFloat(oRange[x][2]("max"));
				var fValue = parseFloat(field.value);
				if (!(fValue >= fMin /*&& fValue <= fMax*/)) {
					if (i == 0) {
						focusField = field;
					}
					fields[i++] = oRange[x][1];
					isValid = false;
				}
			}
		}
		if (fields.length > 0) {
			try{focusField.focus();}catch(e){}
			alert(fields.join('\n'));
		}
		return isValid;
	}
	
//----------------------------------------------------------------------

	/**
	* Check to see if fields are in a valid float range.
	* Fields are not checked if they are disabled.
	* <p>
	* @param form The form validation is taking place on.
	*/
	function validateFloatMaxValue(form) {
		var isValid = true;
		var focusField = null;
		var i = 0;
		var fields = new Array();
		var formName = form.getAttributeNode("name"); 

		oRange = eval('new ' + formName.value + '_floatMaxValue()');
		for (x in oRange) {
			// form[] n?o funciona direito quando o nome
			// dos elements mudam dinamicamente
			//var field = form[oRequired[x][0]];

			var field = null;
			field = formProperties[oRange[x][0]];
			/*
			for (var j = 0; j < form.elements.length; j++){
				element = form.elements[j];
				if(element.name == null) continue;
				if(element.name == oRange[x][0]){
					field = element;
				}
			}
			*/
			if ((field.type == 'hidden' ||
				field.type == 'text' || field.type == 'textarea') &&
				(field.value.length > 0)  &&
				 field.disabled == false) {
		
				//var fMin = parseFloat(oRange[x][2]("min"));
				var value = field.value;
				value = value.replace(".","").replace(",",".");
				var fMax = parseFloat(oRange[x][2]("max"));
				var fValue = parseFloat(value);
				if (!(/*fValue >= fMin &&*/ fValue <= fMax)) {
					if (i == 0) {
						focusField = field;
					}
					fields[i++] = oRange[x][1];
					isValid = false;
				}
			}
		}
		if (fields.length > 0) {
			try{focusField.focus();}catch(e){}
			alert(fields.join('\n'));
		}
		return isValid;
	}
	

//----------------------------------------------------------------------

	/**
	* Check to see if fields are a valid email address.
	* Fields are not checked if they are disabled.
	* <p>
	* @param form The form validation is taking place on.
	*/
	function validateEmail(form) {
		var bValid = true;
		var focusField = null;
		var i = 0;
		var fields = new Array();
		var formName = form.getAttributeNode("name");


		oEmail = eval('new ' + formName.value + '_email()');

		for (x in oEmail) {
			// form[] n?o funciona direito quando o nome
			// dos elements mudam dinamicamente
			//var field = form[oRequired[x][0]];

			var field = null;
			field = formProperties[oEmail[x][0]];
			/*
			for (var j = 0; j < form.elements.length; j++){
				element = form.elements[j];
				if(element.name == null) continue;
				if(element.name == oEmail[x][0]){
					field = element;
				}
			}
			*/
			if ((field.type == 'hidden' || 
				 field.type == 'text' ||
				 field.type == 'textarea') &&
				(field.value.length > 0) &&
				field.disabled == false) {
				if (!checkEmail(field.value)) {
					if (i == 0) {
						focusField = field;
					}
					fields[i++] = oEmail[x][1];
					bValid = false;
				}
			}
		}
		if (fields.length > 0) {
			try{focusField.focus();}catch(e){}
			alert(fields.join('\n'));
		}
		return bValid;
	}
	
	
	/**
	 * Reference: Sandeep V. Tamhankar (stamhankar@hotmail.com),
	 * http://javascript.internet.com
	 */
	function checkEmail(emailStr) {
		if (emailStr.length == 0) {
			return true;
		}
		var emailPat = /^(.+)@(.+)$/;
		var specialChars = "\\(\\)<>@,;:\\\\\\\"\\.\\[\\]";
		var validChars = "\[^\\s" + specialChars + "\]";
		var quotedUser = "(\"[^\"]*\")";
		var ipDomainPat = /^(\d{1,3})[.](\d{1,3})[.](\d{1,3})[.](\d{1,3})$/;
		var atom = validChars + '+';
		var word = "(" + atom + "|" + quotedUser + ")";
		var userPat = new RegExp("^" + word + "(\\." + word + ")*$");
		var domainPat = new RegExp("^" + atom + "(\\." + atom + ")*$");
		var matchArray = emailStr.match(emailPat);
		if (matchArray == null) {
			return false;
		}
		var user = matchArray[1];
		var domain = matchArray[2];
		if (user.match(userPat) == null) {
			return false;
		}
		var IPArray = domain.match(ipDomainPat);
		if (IPArray != null) {
			for ( var i = 1; i <= 4; i++) {
				if (IPArray[i] > 255) {
					return false;
				}
			}
			return true;
		}
		var domainArray = domain.match(domainPat);
		if (domainArray == null) {
			return false;
		}
		var atomPat = new RegExp(atom, "g");
		var domArr = domain.match(atomPat);
		var len = domArr.length;
		if ((domArr[domArr.length - 1].length < 2)
				|| (domArr[domArr.length - 1].length > 3)) {
			return false;
		}
		if (len < 2) {
			return false;
		}
		return true;
	}

// ----------------------------------------------------------------------

	/**
	 * Check to see if fields are a valid date. Fields are not checked if they
	 * are disabled.
	 * <p>
	 * 
	 * @param form
	 *            The form validation is taking place on.
	 */
	function validateDate(form) {
		var bValid = true;
		var focusField = null;
		var i = 0;
		var fields = new Array();
		var formName = form.getAttributeNode("name"); 
		var fieldObjs = new Array();

		oDate = eval('new ' + formName.value + '_DateValidations()');

		for (x in oDate) {
			// form[] n?o funciona direito quando o nome
			// dos elements mudam dinamicamente
			//var field = form[oRequired[x][0]];

			var field = null;
			field = formProperties[oDate[x][0]];
			/*
			for (var j = 0; j < form.elements.length; j++){
				element = form.elements[j];
				if(element.name == null) continue;
				if(element.name == oDate[x][0]){
					field = element;
				}
			}
			 */
		
			var value = field.value;
			var datePattern = formProperties[oDate[x][0]+'_datePattern'].value;
			if (datePattern == null){
				datePattern = oDate[x][2]("datePatternStrict");
			}
			// try loose pattern
			if (datePattern == null)
				datePattern = oDate[x][2]("datePattern");
			if ((field.type == 'hidden' ||
				field.type == 'text' ||
				field.type == 'textarea') && (value.length > 0) && (datePattern.length > 0) && field.disabled == false) {
				var MONTH = "MM";
				var DAY = "dd";
				var YEAR = "yyyy";
				var SHORTYEAR = "yy";
				var HOUR = "HH";
				var MINUTE = "mm";
				var SECOND = "ss";
				
				var orderMonth  = datePattern.indexOf(MONTH);
				var orderDay	= datePattern.indexOf(DAY);
				var orderYear   = datePattern.indexOf(YEAR);
				var orderHour   = datePattern.indexOf(HOUR);
				var orderMinute = datePattern.indexOf(MINUTE);
				var orderSecond = datePattern.indexOf(SECOND);
				var orderShortYear = datePattern.indexOf(SHORTYEAR);
				
				if(datePattern.indexOf('hh') > 0){
					alert('Hora \'hh\' não suportada! Utilize \'HH\' para formatar hora.');
				}
				
				var regex = "^"+datePattern+"$";
				
				regex = regex.replace("\\", "\\\\");
				regex = regex.replace("{", "\\{");
				regex = regex.replace("}", "\\}");
				regex = regex.replace("[", "\\[");
				regex = regex.replace("]", "\\]");
				regex = regex.replace("-", "\\-");
				regex = regex.replace("+", "\\+");
				regex = regex.replace(".", "\\.");
				
				if(orderMonth != -1){
					regex = regex.replace(MONTH, '\\d{2}');
				}
				if(orderDay != -1){
					regex = regex.replace(DAY, '\\d{2}');
				}
				if(orderYear != -1){
					regex = regex.replace(YEAR, '\\d{4}');
				}
				if(orderHour != -1){
					regex = regex.replace(HOUR, '\\d{2}');
				}
				if(orderMinute != -1){
					regex = regex.replace(MINUTE, '\\d{2}');
				}
				if(orderSecond != -1){
					regex = regex.replace(SECOND, '\\d{2}');
				}
				if(orderShortYear != -1 && orderYear == -1){
					regex = regex.replace(SHORTYEAR, '\\d{2}');
				}
				
				var matched;
				try {
					 matched = new RegExp(regex).exec(value);
				}catch(e){
				 	alert('Erro ao verificar regex '+regex+' do pattern '+datePattern);
				}
				
				// alert(regex + '\n' + matched);
				if (matched != null) {
					// o value é conforme com o pattern.. precisamos verificar os valores
					var month = -1;
					var day = -1;
					var year = -1;
					var hour = -1;
					var minute = -1;
					var second = -1;
	
					if (orderMonth != -1) {
						month = asInt(value.substring(datePattern.indexOf(MONTH),
								datePattern.indexOf(MONTH) + 2));
					}
					if (orderDay != -1) {
						day = asInt(value.substring(datePattern.indexOf(DAY),
								datePattern.indexOf(DAY) + 2));
					}
					if (orderYear != -1) {
						year = asInt(value.substring(datePattern.indexOf(YEAR),
								datePattern.indexOf(YEAR) + 4));
					}
					if (orderHour != -1) {
						hour = asInt(value.substring(datePattern.indexOf(HOUR),
								datePattern.indexOf(HOUR) + 2));
					}
					if (orderMinute != -1) {
						minute = asInt(value.substring(datePattern.indexOf(MINUTE),
								datePattern.indexOf(MINUTE) + 2));
					}
					if (orderSecond != -1) {
						second = asInt(value.substring(datePattern.indexOf(SECOND),
								datePattern.indexOf(SECOND) + 2));
					}
					if (orderShortYear != -1 && orderYear == -1) {
						hour = asInt(value.substring(
								datePattern.indexOf(SHORTYEAR), datePattern
										.indexOf(SHORTYEAR) + 2));
					}
					// window.status = datePattern+' '+day+'/'+month+'/'+year+''+hour+':'+minute+':'+second;
	
					if (!isValidDate(day, month, year, hour, minute, second)) {
						if (i == 0) {
							focusField = field;
						}
	
						fields[i++] = oDate[x][1];
						fieldObjs[i] = form[oDate[x][0]];
						bValid = false;
					}
				} else {
					if (i == 0) {
						focusField = field;
					}
	
					fields[i++] = oDate[x][1];
					fieldObjs[i] = form[oDate[x][0]];
					bValid = false;
				}
				
				/*
				var orderMonth = datePattern.indexOf(MONTH);
				var orderDay = datePattern.indexOf(DAY);
				var orderYear = datePattern.indexOf(YEAR);
				if ((orderDay < orderYear && orderDay > orderMonth)) {
					var iDelim1 = orderMonth + MONTH.length;
					var iDelim2 = orderDay + DAY.length;
					var delim1 = datePattern.substring(iDelim1, iDelim1 + 1);
					var delim2 = datePattern.substring(iDelim2, iDelim2 + 1);
					if (iDelim1 == orderDay && iDelim2 == orderYear) {
						dateRegexp = new RegExp("^(\\d{2})(\\d{2})(\\d{4})$");
					} else if (iDelim1 == orderDay) {
						dateRegexp = new RegExp("^(\\d{2})(\\d{2})[" + delim2 + "](\\d{4})$");
					} else if (iDelim2 == orderYear) {
						dateRegexp = new RegExp("^(\\d{2})[" + delim1 + "](\\d{2})(\\d{4})$");
					} else {
						dateRegexp = new RegExp("^(\\d{2})[" + delim1 + "](\\d{2})[" + delim2 + "](\\d{4})$");
					}
					var matched = dateRegexp.exec(value);
					if(matched != null) {
						if (!isValidDate(matched[2], matched[1], matched[3])) {
							if (i == 0) {
								focusField = field;
							}
							fields[i++] = oDate[x][1];
							bValid =  false;
						}
					} else {
						if (i == 0) {
							focusField = field;
						}
						fields[i++] = oDate[x][1];
						bValid =  false;
					}
				} else if ((orderMonth < orderYear && orderMonth > orderDay)) {
					var iDelim1 = orderDay + DAY.length;
					var iDelim2 = orderMonth + MONTH.length;
					var delim1 = datePattern.substring(iDelim1, iDelim1 + 1);
					var delim2 = datePattern.substring(iDelim2, iDelim2 + 1);
					if (iDelim1 == orderMonth && iDelim2 == orderYear) {
						dateRegexp = new RegExp("^(\\d{2})(\\d{2})(\\d{4})$");
					} else if (iDelim1 == orderMonth) {
						dateRegexp = new RegExp("^(\\d{2})(\\d{2})[" + delim2 + "](\\d{4})$");
					} else if (iDelim2 == orderYear) {
						dateRegexp = new RegExp("^(\\d{2})[" + delim1 + "](\\d{2})(\\d{4})$");
					} else {
						dateRegexp = new RegExp("^(\\d{2})[" + delim1 + "](\\d{2})[" + delim2 + "](\\d{4})$");
					}
					var matched = dateRegexp.exec(value);
					if(matched != null) {
						if (!isValidDate(matched[1], matched[2], matched[3])) {
							if (i == 0) {
						focusField = field;
							}
							fields[i++] = oDate[x][1];
							bValid =  false;
						}
					} else {
						if (i == 0) {
							focusField = field;
						}
						fields[i++] = oDate[x][1];
						bValid =  false;
					}
				} else if ((orderMonth > orderYear && orderMonth < orderDay)) {
					var iDelim1 = orderYear + YEAR.length;
					var iDelim2 = orderMonth + MONTH.length;
					var delim1 = datePattern.substring(iDelim1, iDelim1 + 1);
					var delim2 = datePattern.substring(iDelim2, iDelim2 + 1);
					if (iDelim1 == orderMonth && iDelim2 == orderDay) {
						dateRegexp = new RegExp("^(\\d{4})(\\d{2})(\\d{2})$");
					} else if (iDelim1 == orderMonth) {
						dateRegexp = new RegExp("^(\\d{4})(\\d{2})[" + delim2 + "](\\d{2})$");
					} else if (iDelim2 == orderDay) {
						dateRegexp = new RegExp("^(\\d{4})[" + delim1 + "](\\d{2})(\\d{2})$");
					} else {
						dateRegexp = new RegExp("^(\\d{4})[" + delim1 + "](\\d{2})[" + delim2 + "](\\d{2})$");
					}
					var matched = dateRegexp.exec(value);
					if(matched != null) {
						if (!isValidDate(matched[3], matched[2], matched[1])) {
							if (i == 0) {
								focusField = field;
							}
							fields[i++] = oDate[x][1];
							bValid =  false;
						}
					} else {
						if (i == 0) {
							focusField = field;
						}
						fields[i++] = oDate[x][1];
						bValid =  false;
					}
				} else {
					if (i == 0) {
						focusField = field;
					}

					fields[i++] = oDate[x][1];
					fieldObjs[i] = form[oDate[x][0]];
					bValid =  false;
				}
				*/
			}
		}
		if (fields.length > 0) {
			invalidFields(form, fieldObjs, fields, 'date');
		}
		return bValid;
	}
	
	function asInt(valor){
		if(valor.charAt(0) == '0'){
			return parseInt(valor.substring(1));
		} else {
			return parseInt(valor);
		}
	}
	
	function isValidDate(day, month, year, hour, minute, second) {
		//alert(day+'/'+month+'/'+year+' '+hour+':'+minute+':'+second);
		if(day == -1){
			day = 1;
		}
		if(month == -1){
			month = 1;
		}
		if(year == -1){
			year = 1900;
		}
		if(hour == -1){
			hour = 0;
		}
		if(minute == -1){
			minute = 0;
		}
		if(second == -1){
			second = 0;
		}
		if (month < 1 || month > 12) {
			return false;
		}
		if (day < 1 || day > 31) {
			return false;
		}
		if ((month == 4 || month == 6 || month == 9 || month == 11) &&
			(day == 31)) {
			return false;
		}
		if (month == 2) {
			var leap = (year % 4 == 0 &&
			   (year % 100 != 0 || year % 400 == 0));
			if (day>29 || (day == 29 && !leap)) {
				return false;
			}
		}
		if(hour > 24){
			return false;
		}
		if(minute > 59){
			return false;
		}
		if(second > 59){
			return false;
		}
		return true;
	}
	
//----------------------------------------------------------------------

	/**
	* Check to see if fields are a valid creditcard number based on Luhn checksum.
	* Fields are not checked if they are disabled.
	* <p>
	* @param form The form validation is taking place on.
	*/
	function validateCreditCard(form) {
		var bValid = true;
		var focusField = null;
		var i = 0;
		var fields = new Array();
		var formName = form.getAttributeNode("name");

		oCreditCard = eval('new ' + formName.value + '_creditCard()');

		for (x in oCreditCard) {

			var field = null;
			field = formProperties[oCreditCard[x][0]];
			/*
			for (var j = 0; j < form.elements.length; j++){
				element = form.elements[j];
				if(element.name == null) continue;
				if(element.name == oCreditCard[x][0]){
					field = element;
				}
			}
			*/
		
		
			if ((field.type == 'text' ||
				 field.type == 'textarea') &&
				(field.value.length > 0)  &&
				 field.disabled == false) {
				if (!luhnCheck(field.value)) {
					if (i == 0) {
						focusField = field;
					}
					fields[i++] = field;
					bValid = false;
				}
			}
		}
		if (fields.length > 0) {
			try{focusField.focus();}catch(e){}
			alert(fields.join('\n'));
		}
		return bValid;
	}

	/**
	 * Checks whether a given credit card number has a valid Luhn checksum.
	 * This allows you to spot most randomly made-up or garbled credit card numbers immediately.
	 * Reference: http://www.speech.cs.cmu.edu/~sburke/pub/luhn_lib.html
	 */
	function luhnCheck(cardNumber) {
		if (isLuhnNum(cardNumber)) {
			var no_digit = cardNumber.length;
			var oddoeven = no_digit & 1;
			var sum = 0;
			for (var count = 0; count < no_digit; count++) {
				var digit = parseInt(cardNumber.charAt(count));
				if (!((count & 1) ^ oddoeven)) {
					digit *= 2;
					if (digit > 9) digit -= 9;
				};
				sum += digit;
			};
			if (sum == 0) return false;
			if (sum % 10 == 0) return true;
		};
		return false;
	}

	function isLuhnNum(argvalue) {
		argvalue = argvalue.toString();
		if (argvalue.length == 0) {
			return false;
		}
		for (var n = 0; n < argvalue.length; n++) {
			if ((argvalue.substring(n, n+1) < "0") ||
				(argvalue.substring(n,n+1) > "9")) {
				return false;
			}
		}
		return true;
	}
	
//----------------------------------------------------------------------

	/**
	*  Verifica determinado campo ? um cpf v?lido
	* <p>
	* @param form The form validation is taking place on.
	*/

	function validateCpf(form) {
		var isValid = true;
		var focusField = null;
		var i = 0;
		var fields = new Array();
		var fieldObjs = new Array();
		var formName = form.getAttributeNode("name");

		oCpf = eval('new ' + formName.value + '_cpf()');

		for (x in oCpf) {
			// form[] n?o funciona direito quando o nome
			// dos elements mudam dinamicamente
			//var field = form[oCpf[x][0]];

			var field = null;
			field = formProperties[oCpf[x][0]];
			/*
			for (var j = 0; j < form.elements.length; j++){
				element = form.elements[j];
				if(element.name == null) continue;
				if(element.name == oCpf[x][0]){
					field = element;
				}
			}
			*/
			if ((field.type == 'hidden' ||
				field.type == 'text' ||
				field.type == 'password' ||
				field.type == 'textarea') &&
				field.disabled == false) {

				if(field.value.replace( /\s*/, "" ).length==0) { 
					continue;
				}


				if (!DigitoCPF(ApenasNum(field.value))) {
					if (i == 0) {
						focusField = field;
					}
					fields[i++] = oCpf[x][1];
					isValid = false;
				}
			}
		}
		if (fields.length > 0) {
			invalidFields(form, fieldObjs, fields, 'cpf');
		}
		return isValid;
	}

	//Fun??o para C?lculo do Digito do CPF/CNPJ
	function DigitoCPF(numCIC) {
		var numDois = numCIC.substring(numCIC.length - 2, numCIC.length);
		var novoCIC = numCIC.substring(0, numCIC.length - 2);
		switch (numCIC.length) {
		case 11:
			numLim = 11;
			break;
		default:
			return false;
		}
		//
		var numSoma = 0;
		var Fator = 1;
		for ( var i = novoCIC.length - 1; i >= 0; i--) {
			Fator = Fator + 1;
			if (Fator > numLim) {
				Fator = 2;
			}
			numSoma = numSoma + (Fator * Number(novoCIC.substring(i, i + 1)));
		}
		numSoma = numSoma / 11;
		var numResto = Math.round(11 * (numSoma - Math.floor(numSoma)));
		if (numResto > 1) {
			numResto = 11 - numResto;
		} else {
			numResto = 0;
		}
		// -- Primeiro d?gito calculado. Far? parte do novo c?lculo.
		// --
		var numDigito = String(numResto);
		novoCIC = novoCIC.concat(numResto);
		// --
		numSoma = 0;
		Fator = 1;
		for ( var i = novoCIC.length - 1; i >= 0; i--) {
			Fator = Fator + 1;
			if (Fator > numLim) {
				Fator = 2;
			}
			numSoma = numSoma + (Fator * Number(novoCIC.substring(i, i + 1)));
		}
		numSoma = numSoma / 11;
		numResto = numResto = Math.round(11 * (numSoma - Math.floor(numSoma)));
		if (numResto > 1) {
			numResto = 11 - numResto;
		} else {
			numResto = 0;
		}
		// -- Segundo d?gito calculado.
		numDigito = numDigito.concat(numResto);
		//
		if (numDigito == numDois) {
			return true;
		} else {
			return false;
		}
	}

//----------------------------------------------------------------------

	/**
	*  Verifica determinado campo ? um cnpj v?lido
	* <p>
	* @param form The form validation is taking place on.
	*/

	function validateCnpj(form) {
		var isValid = true;
		var focusField = null;
		var i = 0;
		var fields = new Array();
		var fieldObjs = new Array();
		var formName = form.getAttributeNode("name");

		oCnpj = eval('new ' + formName.value + '_cnpj()');

		for (x in oCnpj) {
			
			// form[] n?o funciona direito quando o nome
			// dos elements mudam dinamicamente
			//var field = form[oCnpj[x][0]];

			var field = null;
			field = formProperties[oCnpj[x][0]];
			 
			/*
			for (var j = 0; j < form.elements.length; j++){
				element = form.elements[j];
				if(element.name == null) continue;
				if(element.name == oCnpj[x][0]){
					field = element;
				}
			}
			*/
			
			if ((field.type == 'hidden' ||
				field.type == 'text' ||
				field.type == 'password' ||
				field.type == 'textarea') &&
				field.disabled == false) {
 
				if(field.value.replace( /\s*/, "" ).length==0) { 
					continue;
				}

				if (!digitoCNPJ(ApenasNum(field.value))) {
					if (i == 0) {
						focusField = field;
					}
					fields[i++] = oCnpj[x][1];
					isValid = false;
				}
				
			}
		}
		if (fields.length > 0) {
			invalidFields(form, fieldObjs, fields, 'cnpj');
		}

		return isValid;
	}


//-----------------------------------------------------------------------------
	
	//Fun??o para C?lculo do Digito do CPF/CNPJ
	function digitoCNPJ(numCIC) {
	
		var numDois = numCIC.substring(numCIC.length-2, numCIC.length);
		var novoCIC = numCIC.substring(0, numCIC.length-2);
	
		
		switch (numCIC.length){
			case 15 :
				numLim = 9;
				break;	
			case 14 :
				novoCIC = '0' + novoCIC;
				numLim = 9;
				break;
			default :
				return false;
		}
		
		
		//
		var numSoma = 0;
		var Fator = 1;
		for (var i=novoCIC.length-1; i>=0; i--) {
			Fator = Fator + 1;
			if (Fator > numLim) {
				Fator = 2;
			}
			numSoma = numSoma + (Fator * Number(novoCIC.substring(i, i+1)));
		}
		numSoma = numSoma/11;
		var numResto = Math.round( 11 * (numSoma - Math.floor(numSoma)));
		if (numResto > 1) {
			numResto = 11 - numResto;
		}
		else {
			numResto = 0;
		}
		//-- Primeiro d?gito calculado.  Far? parte do novo c?lculo.
		//--
		var numDigito = String(numResto);
		novoCIC = novoCIC.concat(numResto);
		//--
		numSoma = 0;
		Fator = 1;
		for (var i=novoCIC.length-1; i>=0; i--) {
			Fator = Fator + 1;
			if (Fator > numLim) {
				Fator = 2;
			}
			numSoma = numSoma + (Fator * Number(novoCIC.substring(i, i+1)));
		}
		numSoma = numSoma/11;
		numResto = numResto = Math.round( 11 * (numSoma - Math.floor(numSoma)));
		if (numResto > 1) {
			numResto = 11 - numResto;
		}
		else {
			numResto = 0;
		}
		//-- Segundo d?gito calculado.
		numDigito = numDigito.concat(numResto);
		//
		if (numDigito == numDois) {
			return true;
		}
		else {
			return false;
		}
	}

//----------------------------------------------------------------------

	/**
	*  Verifica determinado campo ? um cep v?lido
	* <p>
	* @param form The form validation is taking place on.
	*/

	function validateCep(form) {
		var isValid = true;
		var focusField = null;
		var i = 0;
		var fields = new Array();
		var fieldObjs = new Array();
		var formName = form.getAttributeNode("name");

		oCep = eval('new ' + formName.value + '_cep()');

		for (x in oCep) {
			// form[] n?o funciona direito quando o nome
			// dos elements mudam dinamicamente
			//var field = form[oCep[x][0]];

			var field = null;
			field = formProperties[oCep[x][0]];
			/*
			for (var j = 0; j < form.elements.length; j++){
				element = form.elements[j];
				if(element.name == null) continue;
				if(element.name == oCep[x][0]){
					field = element;
				}
			}
			*/
			if ((field.type == 'hidden' ||
				field.type == 'text' ||
				field.type == 'password' ||
				field.type == 'textarea') &&
				field.disabled == false) {

				if(field.value.replace( /\s*/, "" ).length==0) return true;

				if (!field.value.match(/[0-9]{4}-[0-9]{3}/)) {
					if (i == 0) {
						focusField = field;
					}
					fields[i++] = oCep[x][1];
					isValid = false;
				}
			}
		}
		if (fields.length > 0) {
			invalidFields(form, fieldObjs, fields, 'cep');
		}
		return isValid;
	}
	
//----------------------------------------------------------------------

	/**
	* Check to see if fields are a valid byte.
	* Fields are not checked if they are disabled.
	* <p>
	* @param form The form validation is taking place on.
	*/
	function validateByte(form) {
		var bValid = true;
		var focusField = null;
		var i = 0;
		var fields = new Array();
		var formName = form.getAttributeNode("name"); 
		oByte = eval('new ' + formName.value + '_ByteValidations()');

		for (x in oByte) {
			// form[] n?o funciona direito quando o nome
			// dos elements mudam dinamicamente
			//var field = form[oRequired[x][0]];

			var field = null;
			field = formProperties[oByte[x][0]];
			/*
			for (var j = 0; j < form.elements.length; j++){
				element = form.elements[j];
				if(element.name == null) continue;
				if(element.name == oByte[x][0]){
					field = element;
				}
			}
			*/

			if ((field.type == 'hidden' ||
				field.type == 'text' ||
				field.type == 'textarea' ||
				field.type == 'select-one' ||
				field.type == 'radio')  &&
				field.disabled == false) {

				var value = '';
				// get field's value
				if (field.type == "select-one") {
					var si = field.selectedIndex;
					if (si >= 0) {
						value = field.options[si].value;
					}
				} else {
					value = field.value;
				}

				if (value.length > 0) {
					if (!isAllDigits(value)) {
						bValid = false;
						if (i == 0) {
							focusField = field;
						}
						fields[i++] = oByte[x][1];

					} else {

						var iValue = parseInt(value);
						if (isNaN(iValue) || !(iValue >= -128 && iValue <= 127)) {
							if (i == 0) {
								focusField = field;
							}
							fields[i++] = oByte[x][1];
							bValid = false;
						}
					}
				}

			}
		}
		if (fields.length > 0) {
			try{focusField.focus();}catch(e){}
			alert(fields.join('\n'));
		}
		return bValid;
	}
	
//----------------------------------------------------------------------

	/**
	* Check to see if fields are a valid using a regular expression.
	* Fields are not checked if they are disabled.
	* <p>
	* @param form The form validation is taking place on.
	*/
	function validateMask(form) {
		var isValid = true;
		var focusField = null;
		var i = 0;
		var fields = new Array();
		var formName = form.getAttributeNode("name"); 

		oMasked = eval('new ' + formName.value + '_mask()');	  
		for (x in oMasked) {
			// form[] n?o funciona direito quando o nome
			// dos elements mudam dinamicamente
			//var field = form[oRequired[x][0]];

			var field = null;
			field = formProperties[oMasked[x][0]];
			/*
			for (var j = 0; j < form.elements.length; j++){
				element = form.elements[j];
				if(element.name == null) continue;
				if(element.name == oMasked[x][0]){
					field = element;
				}
			}
			*/

			if ((field.type == 'hidden' ||
				field.type == 'text' ||
				 field.type == 'textarea' ||
				 field.type == 'file') &&
				 (field.value.length > 0) &&
				 field.disabled == false) {

				if (!matchPattern(field.value, oMasked[x][2]("mask"))) {
					if (i == 0) {
						focusField = field;
					}
					fields[i++] = oMasked[x][1];
					isValid = false;
				}
			}
		}

		if (fields.length > 0) {
		   try{focusField.focus();}catch(e){}
		   alert(fields.join('\n'));
		}
		return isValid;
	}

	function matchPattern(value, mask) {
		return mask.exec(value);
	}
	
//----------------------------------------------------------------------

	/**
	*  Check to see if fields must contain a value.
	* Fields are not checked if they are disabled.
	* <p>
	* @param form The form validation is taking place on.
	*/

	function validateRequired(form) {
		var isValid = true;
		var focusField = null;
		var i = 0;
		var fields = new Array();
		var fieldObjs = new Array();
		var formName = form.getAttributeNode("name");
		oRequired = eval('new ' + formName.value + '_required()');

		for (x in oRequired) {
			// form[] n?o funciona direito quando o nome
			// dos elements mudam dinamicamente
			//var field = form[oRequired[x][0]];
			//alert('required loop');
			var field = null;
			var radiofields = new Array();
			
			
			var fprox = formProperties[oRequired[x][0]];
			if(fprox){
				if(fprox.isArray){
					radiofields = fprox;
				} else {
					field = fprox;
				}
			}
			/*
			for (var j = 0; j < form.elements.length; j++){
				//alert('required loop item  '+j+' de '+form.elements.length);
				element = form.elements[j];
				if(element.name == null) continue;
				if(element.name == oRequired[x][0]){
					field = element;
					if(element.type == 'radio'){
						radiofields[radiofields.length] = element;
					}
				}
			}*/
			if(radiofields.length > 0){
				var haschecked = false;
				for(var r = 0; r < radiofields.length; r++){
					//alert('loop radios');
					var radio = radiofields[r];
					if(radio.checked){
						haschecked = true;
						//alert('selected: '+radio.value+'  '+radio.name);
					} //else {
						//alert('not selected: '+radio.value+'  '+radio.name);
					//}
				}
				if(!haschecked){
					fields[i++] = oRequired[x][1];
					fieldObjs[i] = radiofields[0];
					isValid = false; 	
					//alert('Faltando '+oRequired[x][0]);
				}
			} else if ((field != null && (field.type == 'hidden' ||
				field.type == 'text' ||
				field.type == 'textarea' ||
				field.type == 'file' ||
				//field.type == 'checkbox' ||
				field.type == 'select-one' ||
				field.type == 'password')) &&
				field.disabled == false) {
				
				var value = '';
				// get field's value
				if (field.type == "select-one") {
					var si = field.selectedIndex;
					if (si >= 0) {
						value = field.options[si].value;
					}
				} else if (field.type == 'checkbox') {
					if (field.checked) {
						value = field.value;
					}
				} else {
					value = field.value;
				}
				if (trim(value).length == 0 || value == '<null>') {
					if (field.type == 'file') {
						if	(document.getElementById(field.name+'_excludeField').value == 'true'
								|| document.getElementById(field.name+'_div').innerHTML == '[vazio]') {
							
							if (i == 0) {
								focusField = field;
							}
							fields[i++] = oRequired[x][1];
							fieldObjs[i] = form[oRequired[x][0]];
							isValid = false;
						}
					} else {
							if (i == 0) {
								focusField = field;
							}
							fields[i++] = oRequired[x][1];
							fieldObjs[i] = form[oRequired[x][0]];
							isValid = false;
					}
				}
			} else if (field != null && field.type == "select-multiple") { 
				var numOptions = field.options.length;
				lastSelected=-1;
				for(loop=numOptions-1;loop>=0;loop--) {
					if(field.options[loop].selected) {
						lastSelected = loop;
						value = field.options[loop].value;
						break;
					}
				}
				if(lastSelected < 0 || trim(value).length == 0) {
					if(i == 0) {
						focusField = field;
					}
					fields[i++] = oRequired[x][1];
					fieldObjs[i] = form[oRequired[x][0]];
					isValid=false;
				}
			} else if (field != null && (field.length > 0) && (field[0].type == 'radio' || field[0].type == 'checkbox')) {
				isChecked=-1;
				for (loop=0;loop < field.length;loop++) {
					if (field[loop].checked) {
						isChecked=loop;
						break; // only one needs to be checked
					}
				}
				if (isChecked < 0) {
					if (i == 0) {
						focusField = field[0];
					}
					fields[i++] = oRequired[x][1];
					fieldObjs[i] = form[oRequired[x][0]];
					isValid=false;
				}
			}
		}
		if (fields.length > 0) {
			invalidFields(form, fieldObjs, fields, 'required');
		}
		return isValid;
	}


	//Trim whitespace from left and right sides of s.
	function trim(s) {
		return s.replace(/^\s*/, "").replace(/\s*$/, "");
	}

//----------------------------------------------------------------------

	/**
	*  Verifica determinado campo ? um telefone v?lido
	* <p>
	* @param form The form validation is taking place on.
	*/

	function validateTelefone(form) {
		var isValid = true;
		var focusField = null;
		var i = 0;
		var fields = new Array();
		var fieldObjs = new Array();
		var formName = form.getAttributeNode("name");

		oTelefone = eval('new ' + formName.value + '_telefone()');

		for (x in oTelefone) {
			// form[] n?o funciona direito quando o nome
			// dos elements mudam dinamicamente
			//var field = form[oTelefone[x][0]];

			var field = null;
			field = formProperties[oTelefone[x][0]];
			/*
			for (var j = 0; j < form.elements.length; j++){
				element = form.elements[j];
				if(element.name == null) continue;
				if(element.name == oTelefone[x][0]){
					field = element;
				}
			}
			*/
			
			if ((field.type == 'hidden' ||
				field.type == 'text' ||
				field.type == 'password' ||
				field.type == 'textarea') &&
				field.disabled == false) {

				if(field.value.replace( /\s*/, "" ).length==0) return true;
	
				if (!field.value.match(/\([0-9][0-9]\)( )?[0-9]{4,5}-[0-9]{4}/)) {
					if (i == 0) {
						focusField = field;
					}
					fields[i++] = oTelefone[x][1];
					isValid = false;
				}
			}
		}
		if (fields.length > 0) {
			invalidFields(form, fieldObjs, fields, 'telefone');
		}
		return isValid;
	}
	
//----------------------------------------------------------------------

	/**
	*  Verifica determinado campo ? um time v?lido
	* <p>
	* @param form The form validation is taking place on.
	*/

	function validateTime(form) {
		var isValid = true;
		var focusField = null;
		var i = 0;
		var fields = new Array();
		var fieldObjs = new Array();
		var formName = form.getAttributeNode("name");

		oTime = eval('new ' + formName.value + '_time()');

		for (x in oTime) {
			// form[] n?o funciona direito quando o nome
			// dos elements mudam dinamicamente
			//var field = form[oTime[x][0]];

			var field = null;
			field = formProperties[oTime[x][0]];
			/*
			for (var j = 0; j < form.elements.length; j++){
				element = form.elements[j];
				if(element.name == null) continue;
				if(element.name == oTime[x][0]){
					field = element;
				}
			}
			*/
			
			if ((field.type == 'hidden' ||
				field.type == 'text' ||
				field.type == 'password' ||
				field.type == 'textarea') &&
				field.disabled == false) {

				if(field.value.replace( /\s*/, "" ).length==0) return true;
	
				if (!verifica_hora(field.value)) {
					if (i == 0) {
						focusField = field;
					}
					fields[i++] = oTime[x][1];
					isValid = false;
				}
			}
		}
		if (fields.length > 0) {
			invalidFields(form, fieldObjs, fields, 'time');
		}
		return isValid;
	}

	function verifica_hora(hour){
		situacao = 1;
		hora = (hour.substring(0,2)); 
		minutos = (hour.substring(3,5)); 
		ponto = (hour.substring(2,3));
		if(hora>24)
			situacao = 0;
		if(minutos>=60)
			situacao = 0;
		if(ponto != ':')
			situacao = 0;
		if(situacao==0){
			hour.value='';
			return false;
		}
		return true;
	}
	
//----------------------------------------------------------------------
