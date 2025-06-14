package isel.leic.group25.websockets.exceptions

import java.lang.Exception

class UnexpectedEventException(): Exception("Received unexpected event from device")