package sistema.sistemadesoportetecnicoit.shared.protocolo;

public enum TipoMensaje {
     // PC2 -> PC1
    REGISTRAR_TICKET,

    // PC3/PC4/PC5 -> PC1
    SOLICITAR_TICKET,

    // PC2 a PC1
    PEDIR_NORMAL,


    //PC3 a PC1
    PEDIR_PRIORIDAD,

    // PC1 -> PC3/PC4/PC5
    ENTREGAR_TICKET,

    // PC3/PC4/PC5 -> PC1
    FINALIZAR_ATENCION,

    // PC1 -> Todos
    ACTUALIZAR_VISTA,

    // PC2 -> PC1
    BUSCAR_DPI,

    // PC1 -> PC2
    RESPUESTA_DPI,
    
    CHAT_MENSAJE,

    OK,
    ERROR,
    DESCONECTAR
}
