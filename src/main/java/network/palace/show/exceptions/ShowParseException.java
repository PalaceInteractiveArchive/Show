package network.palace.show.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Marc
 * @since 8/2/17
 */
@AllArgsConstructor
public class ShowParseException extends Exception {
    @Getter private String reason;
}
