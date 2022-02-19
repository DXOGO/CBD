// Construa, e teste no servidor, uma função em JavaScript que encontre um tipo de
// padrão na lista (e.g., capicuas, sequências, dígitos não repetidos, etc.).

// tentativa de capicua
findCapicua = function(){
    var fullNumber = db.phones.find({},{"display" : 1, "_id" : 0}).toArray() 
    // "display" : "+" + country + "-" + countryNumber     -> all numbers
    
    var capicua = []    // gonna be used to store the numbers with capicua digits and return them

    for (var i = 0; i < fullNumber.length; i++){    // percorrer todos os full numbers e retornar só number
        var number = fullNumber[i]                  // full number (including country and country number)
                            .display                // and we 'split' so number is only the country number
                            .split("-")[1]          // eg. 233000001    
                        

        var len = number.length;
        var mid = Math.floor(len/2);
        var cap = false;

        for ( var j = 0; j < mid; j++ ) {
            
            if (number[j] !== number[len-1-j]) {
                cap = false;
                break
            } else {
                cap = true;
            }

            
        }
         
        if (cap){
            capicua.push(fullNumber[i])
        }
        
    }

    return capicua
}

// tentativa de nº nao repetidos
findNonRepetingDigits = function(){
    var fullNumber = db.phones.find({},{"display" : 1, "_id" : 0}).toArray() 
    // "display" : "+" + country + "-" + countryNumber     -> all numbers
    
    var nonRepeating = []    // gonna be used to store the non repeting numbers and return them
    for (var i = 0; i < fullNumber.length; i++){    // percorrer todos os full numbers e retornar só number
        var number = fullNumber[i]                  // full number (including country and country number)
                            .display                // and we 'split' so number is only the country number
                            .split("-")[1]          // eg. 233000001                           

        var tempArray = []  // guarda cada digito nao repetido do numero
        var repeat = false  

        for (var j = 0; j < number.length ; j++){   // percorrer cada digito do number (eg. 2, 3, 3, 0, 0, ...)
            if (!tempArray.includes(number[j])){    // se o digito n ta no array adiciona 
                tempArray.push(number[j])
            } else {
                repeat = true                       // se está no array ent n faz parte da soluçao pois tem digitos repetidos
                break                              
            }
        }

        if (!repeat){   // se NAO REPETE nenhum adicionamos ao array de numeros com digitos nao repetidos
            nonRepeating.push(fullNumber[i])
        }
    }

    return nonRepeating
}