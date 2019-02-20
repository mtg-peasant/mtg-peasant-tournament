package org.mtgpeasant.tournaments.domain;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;

import java.io.Serializable;

@Slf4j
public class RandomStringIdGenerator implements IdentifierGenerator {
    public static final String NAME = "RANDOM-ID-GEN";

    private static final int PREFERRED_SIZE = 8;

    private static final int MAX_ATTEMPTS_WITH_ONE_LENGTH = 16;

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        RandomValueStringGenerator generator = new RandomValueStringGenerator(PREFERRED_SIZE);
        int attemptsLeft = MAX_ATTEMPTS_WITH_ONE_LENGTH;
        while (true) {
            String id = generator.generate();
            Object existing = Session.class.cast(session).byId(object.getClass()).load(id);
            if (existing != null) {
                attemptsLeft--;
                log.warn("Collision found while generating new user ID, id was {}", id);
            } else {
                log.debug("Generated free user ID: {}", id);
                return id;
            }

            if (attemptsLeft == 0) {
                // increase length by one and go for another try...
                generator.setLength(id.length() + 1);
                attemptsLeft = MAX_ATTEMPTS_WITH_ONE_LENGTH;
                log.warn("Too many collisions found while generating new user ID, set id length to {}", id.length() + 1);
            }
        }
    }
}
