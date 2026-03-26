import relationalStore from '@ohos.data.relationalStore';
import { BusinessError } from '@ohos.base';
import common from '@ohos.app.ability.common';

class UserRow {
  id: number = 0;
  username: string = '';
  password: string = '';
  role: string = '';
}

export default class UserDataProvider {
  private rdbStore: relationalStore.RdbStore | null = null;
  context: common.Context | null = null;

  onCreate(): void {
    if (!this.context) return;
    
    const config: relationalStore.StoreConfig = {
      name: 'vulnerable.db',
      securityLevel: relationalStore.SecurityLevel.S1,
    };

    relationalStore.getRdbStore(this.context, config, (err: BusinessError | null, store: relationalStore.RdbStore | null) => {
      if (err) return;

      this.rdbStore = store;

      if (this.rdbStore) {
        this.rdbStore.executeSql(`
          CREATE TABLE IF NOT EXISTS users (
            id INTEGER PRIMARY KEY,
            username TEXT,
            password TEXT,
            role TEXT
          )
        `);

        this.rdbStore.executeSql(`
          INSERT INTO users VALUES (1, 'admin', 'admin123', 'admin')
        `);
      }
    });
  }

  query(uri: string, predicates: string, columns: string[], callback: (err: BusinessError | null, result?: UserRow[]) => void): void {
    const table = uri.split('/').pop() as string;

    // VULNERABLE: SQL Injection via string concatenation
    // Attack: predicates = "1' OR '1'='1" returns all records
    const sql = `SELECT * FROM ${table} WHERE ${predicates}`; 

    const results: UserRow[] = [];

    this.rdbStore!.querySql(sql, [], (err: BusinessError | null, rs: relationalStore.ResultSet | null) => {
      if (err) return callback(err);

      while (rs!.goToNextRow()) {
        const row = new UserRow();
        row.id = rs!.getLong(0);
        row.username = rs!.getString(1);
        row.password = rs!.getString(2);
        row.role = rs!.getString(3);
        results.push(row);
      }
      
      callback(null, results);
    });
  }

  insert(uri: string, valueBucket: Record<string, Object>, callback: (err: BusinessError | null) => void): void {
    const table = uri.split('/').pop() as string;
    const keys = Object.keys(valueBucket);
    let columns = '';
    let values = '';

    for (let i = 0; i < keys.length; i++) {
      if (i > 0) {
        columns += ', ';
        values += ', ';
      }
      columns += keys[i];
      // VULNERABLE: SQL Injection - no escaping of values
      // Attack: username = "admin', 'hacked@example.com', 'admin'); --
      values += `'${valueBucket[keys[i]]}'`;
    }

    const sql = `INSERT INTO ${table} (${columns}) VALUES (${values})`;
    this.rdbStore!.executeSql(sql, (err: BusinessError | null) => {
      callback(err ? err : null);
    });
  }

  update(uri: string, predicates: string, valueBucket: Record<string, Object>, callback: (err: BusinessError | null) => void): void {
    const table = uri.split('/').pop() as string;
    const keys = Object.keys(valueBucket);
    let setClause = '';

    for (let i = 0; i < keys.length; i++) {
      if (i > 0) setClause += ', ';
      // VULNERABLE: SQL Injection - unsanitized value concatenation
      // Attack: predicates = "1 OR 1=1" modifies all records
      setClause += `${keys[i]} = '${valueBucket[keys[i]]}'`;
    }

    // VULNERABLE: Direct predicates concatenation - SQL Injection
    const sql = `UPDATE ${table} SET ${setClause} WHERE ${predicates}`;
    this.rdbStore!.executeSql(sql, (err: BusinessError | null) => {
      callback(err ? err : null);
    });
  }

  delete(uri: string, predicates: string, callback: (err: BusinessError | null) => void): void {
    const table = uri.split('/').pop() as string;

    // VULNERABLE: SQL Injection via predicates
    // Attack: predicates = "1' OR '1'='1" deletes all records
    const sql = `DELETE FROM ${table} WHERE ${predicates}`;
    this.rdbStore!.executeSql(sql, (err: BusinessError | null) => {
      callback(err ? err : null);
    });
  }
}