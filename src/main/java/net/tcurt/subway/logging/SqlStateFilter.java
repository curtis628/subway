package net.tcurt.subway.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * Suppress log noise from {@code SqlExceptionHelper}.
 *
 * <p>During DB initialization, the loader may attempt to insert duplicate connections. These cause
 * unique constraint violations, which are expected and caught in code. However, Hibernate still
 * logs stack traces for each one via {@code SqlExceptionHelper}.
 *
 * <pre>
 * 12:34:55.774 [Test worker] WARN  o.h.e.jdbc.spi.SqlExceptionHelper - SQL Error: 23505, SQLState: 23505
 * 12:34:55.774 [Test worker] ERROR o.h.e.jdbc.spi.SqlExceptionHelper - Unique index or primary key violation: "PUBLIC.CONSTRAINT_INDEX_E ON PUBLIC.CONNECTION(FROM_ID NULLS FIRST, TO_ID NULLS FIRST, LINE_NAME NULLS FIRST) VALUES ( /* key:21997 *\/ 'place-kencl', 'place-hymnl', 'Green-B')"; SQL statement:
 *   insert into connection (from_id,line_name,to_id,id) values (?,?,?,?) [23505-232]
 * </pre>
 *
 * <p>To keep logs clean, filter out these logs.
 */
public class SqlStateFilter extends Filter<ILoggingEvent> {

  @Override
  public FilterReply decide(ILoggingEvent event) {
    String message = event.getFormattedMessage();
    if (message != null
        && (message.contains("SQL Error: 23505")
            || message.contains("PUBLIC.CONSTRAINT_INDEX_E ON PUBLIC.CONNECTION"))) {
      // Suppress unique constraint violation logs
      return FilterReply.DENY;
    }
    return FilterReply.NEUTRAL;
  }
}
