//Construa uma função/expressão que conte o número de telefones existentes em cada um dos indicativos nacionais (prefix).

findByPrefix = function() {
    c = db.phones.aggregate([ 
                    {$group : {_id : "$components.prefix", no_phones : {$sum : 1}}} 
                ])
    return c
}