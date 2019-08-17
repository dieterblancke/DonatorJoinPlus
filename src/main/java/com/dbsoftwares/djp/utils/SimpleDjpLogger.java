package com.dbsoftwares.djp.utils;

import com.dbsoftwares.djp.DonatorJoinPlus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import java.util.logging.Level;

public class SimpleDjpLogger implements Logger {

    private Logger logger;

    public SimpleDjpLogger() {
        try {
            this.logger = LoggerFactory.getLogger("DonatorJoin+");
        } catch (Exception e) {
            // fail gracefully
        }
    }

    @Override
    public String getName() {
        return "SimpleDjpLogger";
    }

    @Override
    public boolean isTraceEnabled() {
        return false;
    }

    @Override
    public void trace(String s) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void trace(String s, Object o) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void trace(String s, Object o, Object o1) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void trace(String s, Object... objects) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void trace(String s, Throwable throwable) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return false;
    }

    @Override
    public void trace(Marker marker, String s) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void trace(Marker marker, String s, Object o) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void trace(Marker marker, String s, Object o, Object o1) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void trace(Marker marker, String s, Object... objects) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void trace(Marker marker, String s, Throwable throwable) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public boolean isDebugEnabled() {
        return true;
    }

    @Override
    public void debug(String s) {
        if (logger == null) {
            DonatorJoinPlus.i().getLogger().log(Level.INFO, s);
        } else {
            logger.debug(s);
        }
    }

    @Override
    public void debug(String s, Object o) {
        if (logger == null) {
            DonatorJoinPlus.i().getLogger().log(Level.INFO, s, o);
        } else {
            logger.debug(s, o);
        }
    }

    @Override
    public void debug(String s, Object o, Object o1) {
        if (logger == null) {
            DonatorJoinPlus.i().getLogger().log(Level.INFO, s, new Object[]{o, o1});
        } else {
            logger.debug(s, o, o1);
        }
    }

    @Override
    public void debug(String s, Object... objects) {
        if (logger == null) {
            DonatorJoinPlus.i().getLogger().log(Level.INFO, s, objects);
        } else {
            logger.debug(s, objects);
        }
    }

    @Override
    public void debug(String s, Throwable throwable) {
        if (logger == null) {
            DonatorJoinPlus.i().getLogger().log(Level.INFO, s, throwable);
        } else {
            logger.debug(s, throwable);
        }
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return false;
    }

    @Override
    public void debug(Marker marker, String s) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void debug(Marker marker, String s, Object o) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void debug(Marker marker, String s, Object o, Object o1) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void debug(Marker marker, String s, Object... objects) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void debug(Marker marker, String s, Throwable throwable) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public boolean isInfoEnabled() {
        return true;
    }

    @Override
    public void info(String s) {
        if (logger == null) {
            DonatorJoinPlus.i().getLogger().log(Level.INFO, s);
        } else {
            logger.info(s);
        }
    }

    @Override
    public void info(String s, Object o) {
        if (logger == null) {
            DonatorJoinPlus.i().getLogger().log(Level.INFO, s, o);
        } else {
            logger.info(s, o);
        }
    }

    @Override
    public void info(String s, Object o, Object o1) {
        if (logger == null) {
            DonatorJoinPlus.i().getLogger().log(Level.INFO, s, new Object[]{o, o1});
        } else {
            logger.info(s, o, o1);
        }
    }

    @Override
    public void info(String s, Object... objects) {
        if (logger == null) {
            DonatorJoinPlus.i().getLogger().log(Level.INFO, s, objects);
        } else {
            logger.info(s, objects);
        }
    }

    @Override
    public void info(String s, Throwable throwable) {
        if (logger == null) {
            DonatorJoinPlus.i().getLogger().log(Level.INFO, s, throwable);
        } else {
            logger.info(s, throwable);
        }
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return false;
    }

    @Override
    public void info(Marker marker, String s) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void info(Marker marker, String s, Object o) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void info(Marker marker, String s, Object o, Object o1) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void info(Marker marker, String s, Object... objects) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void info(Marker marker, String s, Throwable throwable) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public boolean isWarnEnabled() {
        return true;
    }

    @Override
    public void warn(String s) {
        if (logger == null) {
            DonatorJoinPlus.i().getLogger().log(Level.WARNING, s);
        } else {
            logger.warn(s);
        }
    }

    @Override
    public void warn(String s, Object o) {
        if (logger == null) {
            DonatorJoinPlus.i().getLogger().log(Level.WARNING, s, o);
        } else {
            logger.warn(s, o);
        }
    }

    @Override
    public void warn(String s, Object o, Object o1) {
        if (logger == null) {
            DonatorJoinPlus.i().getLogger().log(Level.WARNING, s, new Object[]{o, o1});
        } else {
            logger.warn(s, o, o1);
        }
    }

    @Override
    public void warn(String s, Object... objects) {
        if (logger == null) {
            DonatorJoinPlus.i().getLogger().log(Level.WARNING, s, objects);
        } else {
            logger.warn(s, objects);
        }
    }

    @Override
    public void warn(String s, Throwable throwable) {
        if (logger == null) {
            DonatorJoinPlus.i().getLogger().log(Level.WARNING, s, throwable);
        } else {
            logger.warn(s, throwable);
        }
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return false;
    }

    @Override
    public void warn(Marker marker, String s) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void warn(Marker marker, String s, Object o) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void warn(Marker marker, String s, Object o, Object o1) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void warn(Marker marker, String s, Object... objects) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void warn(Marker marker, String s, Throwable throwable) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public boolean isErrorEnabled() {
        return true;
    }

    @Override
    public void error(String s) {
        if (logger == null) {
            DonatorJoinPlus.i().getLogger().log(Level.SEVERE, s);
        } else {
            logger.error(s);
        }
    }

    @Override
    public void error(String s, Object o) {
        if (logger == null) {
            DonatorJoinPlus.i().getLogger().log(Level.SEVERE, s, o);
        } else {
            logger.error(s, o);
        }
    }

    @Override
    public void error(String s, Object o, Object o1) {
        if (logger == null) {
            DonatorJoinPlus.i().getLogger().log(Level.SEVERE, s, new Object[]{o, o1});
        } else {
            logger.error(s, o, o1);
        }
    }

    @Override
    public void error(String s, Object... objects) {
        if (logger == null) {
            DonatorJoinPlus.i().getLogger().log(Level.SEVERE, s, objects);
        } else {
            logger.error(s, objects);
        }
    }

    @Override
    public void error(String s, Throwable throwable) {
        if (logger == null) {
            DonatorJoinPlus.i().getLogger().log(Level.SEVERE, s, throwable);
        } else {
            logger.error(s, throwable);
        }
    }
    @Override
    public boolean isErrorEnabled(Marker marker) {
        return false;
    }

    @Override
    public void error(Marker marker, String s) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void error(Marker marker, String s, Object o) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void error(Marker marker, String s, Object o, Object o1) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void error(Marker marker, String s, Object... objects) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void error(Marker marker, String s, Throwable throwable) {
        throw new UnsupportedOperationException("Not implemented.");
    }
}
