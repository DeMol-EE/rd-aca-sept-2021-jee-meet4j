package world.inetum.realdolmen;

import java.sql.Connection;

public interface InTx {

    void apply(Connection c) throws Exception;
}
