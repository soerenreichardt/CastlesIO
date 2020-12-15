package io.castles.core;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

@Component
public class BoardFactoryBean implements FactoryBean<Board> {

    @Override
    public Board getObject() throws Exception {
        return Board.withRandomTile();
    }

    @Override
    public Class<?> getObjectType() {
        return Board.class;
    }
}
