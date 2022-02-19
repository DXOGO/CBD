names = {}  # KEY -> letra inicial (A,B,C,D,..,Z)
            # VALUE -> vezes q um nome aparece no txt com aquela letra inicial

with open ("names.txt", "r") as list:  # ler names.txt
    for n in list:
        f_letter = n[0].upper()          # 1ª letra               
        if f_letter not in names:        # se n estiver no dicionario ('names') ent adiciona
            names[f_letter] =  0         # A = 0, B = 0, ... inicialmente
        
        names[f_letter] += 1             # adiciona 1 smp q encontra um nome c essa letra (key)

with open ("names_counting.txt", "w") as writer: # escrever no names_counting.txt
    for key, value in names.items():
        writer.write(f"SET {key} {value}\n",)   # escreve 'SET key value' no txt
                                                # onde key=A,B,C,...,Z e value=vezes q começa nessa letra  
