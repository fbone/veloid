package com.xirgonium.android.util;

public class SQLRequestPart {

    public static final int END_TYPE_COMMA                   = 1;
    public static final int END_TYPE_OR                      = 2;
    public static final int END_TYPE_AND                     = 3;
    public static final int END_TYPE_POINT_COMMA             = 4;
    public static final int END_TYPE_PARENTHESIS_POINT_COMMA = 5;

    /**
     * 
     * @param field
     * @param pattern
     * @param end
     *            1:, 2: OR 3 : AND
     * @return
     */
    public static String getAttributeLike(String field, String pattern, int end) {
        StringBuffer sqlPart = new StringBuffer(field);
        sqlPart.append(" LIKE '%");
        sqlPart.append(pattern);
        sqlPart.append("%'");
        switch (end) {
            case END_TYPE_COMMA:
                sqlPart.append(", ");
                break;
            case END_TYPE_OR:
                sqlPart.append(" OR ");
                break;
            case END_TYPE_AND:
                sqlPart.append(" AND ");
                break;
            case END_TYPE_POINT_COMMA:
                sqlPart.append(";");
                break;
            case END_TYPE_PARENTHESIS_POINT_COMMA:
                sqlPart.append(");");
                break;
        }
        return sqlPart.toString();
    }

    public static String getSelectFieldsForStations() {
        StringBuffer sqlSelect = new StringBuffer("SELECT ");
        sqlSelect.append(Constant.DB_FIELD_S_ID);
        sqlSelect.append(", ");

        sqlSelect.append(Constant.DB_FIELD_S_NAME);
        sqlSelect.append(", ");

        sqlSelect.append(Constant.DB_FIELD_S_ADDRESS);
        sqlSelect.append(", ");

        sqlSelect.append(Constant.DB_FIELD_S_FULL_ADRESS);
        sqlSelect.append(", ");

        sqlSelect.append(Constant.DB_FIELD_S_LATITUDE);
        sqlSelect.append(", ");

        sqlSelect.append(Constant.DB_FIELD_S_LONGITUDE);
        sqlSelect.append(", ");

        sqlSelect.append(Constant.DB_FIELD_S_OPEN);
        sqlSelect.append(", ");

        sqlSelect.append(Constant.DB_FIELD_S_COMMENT);
        sqlSelect.append(", ");

        sqlSelect.append(Constant.DB_FIELD_S_SIGNET);
        sqlSelect.append(", ");
        
        sqlSelect.append(Constant.DB_FIELD_S_COLOR);
        sqlSelect.append(", ");

        sqlSelect.append(Constant.DB_FIELD_S_NETWORK);

        return sqlSelect.toString();
    }

    public static String getDatabaseCreationSQLForStation() {
        StringBuffer sql = new StringBuffer("CREATE TABLE IF NOT EXISTS ");
        sql.append(Constant.DB_TABLE_STATIONS);
        sql.append(" (");

        sql.append(Constant.DB_FIELD_S_ID);
        sql.append(" VARCHAR, ");

        sql.append(Constant.DB_FIELD_S_NAME);
        sql.append(" VARCHAR, ");

        sql.append(Constant.DB_FIELD_S_NETWORK);
        sql.append(" VARCHAR, ");

        sql.append(Constant.DB_FIELD_S_ADDRESS);
        sql.append(" VARCHAR, ");

        sql.append(Constant.DB_FIELD_S_FULL_ADRESS);
        sql.append(" VARCHAR, ");

        sql.append(Constant.DB_FIELD_S_LATITUDE);
        sql.append(" DECIMAL, ");

        sql.append(Constant.DB_FIELD_S_LONGITUDE);
        sql.append(" DECIMAL, ");

        sql.append(Constant.DB_FIELD_S_COMMENT);
        sql.append(" VARCHAR, ");

        sql.append(Constant.DB_FIELD_S_OPEN);
        sql.append(" VARCHAR, ");
        
        sql.append(Constant.DB_FIELD_S_COLOR);
        sql.append(" DECIMAL, ");

        sql.append(Constant.DB_FIELD_S_SIGNET);
        sql.append(" INTEGER ");

        sql.append(")");

        return sql.toString();
    }
    
    public static String getDatabaseCreationSQLForPlaces() {
      StringBuffer sql = new StringBuffer("CREATE TABLE IF NOT EXISTS ");
      sql.append(Constant.DB_TABLE_PLACE);
      sql.append(" (");

      sql.append(Constant.DB_FIELD_S_ID);
      sql.append(" NUMERIC, ");

      sql.append(Constant.DB_FIELD_S_NAME);
      sql.append(" VARCHAR, ");

      sql.append(Constant.DB_FIELD_S_LATITUDE);
      sql.append(" DECIMAL, ");

      sql.append(Constant.DB_FIELD_S_LONGITUDE);
      sql.append(" DECIMAL ");

      sql.append(")");

      return sql.toString();
  }
    
    public static String addSlashes(String str) {
        if (str == null)
            return "";

        StringBuffer s = new StringBuffer((String) str);
        for (int i = 0; i < s.length(); i++)
            if (s.charAt(i) == '\'')
                s.insert(i++, '\'');
        return s.toString();
    }
    
    

}
