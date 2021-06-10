{ Esse é um programa escrito em p--}
{ O objetivo é calcular o fatorial de um número x dado pelo usuário }
@
program fatorial;
var x, aux, fat: integer;
begin
        read(x);
        fat := 1;
        for aux := 1 to x do
        begin
                fat := fat*aux;
        end;
        write(fat);
end.
