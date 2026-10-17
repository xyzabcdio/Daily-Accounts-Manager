# Daily Accounts Manager — Android source starter

Offline-first Android app source in Java + SQLite. Package: `com.dailyaccounts.manager`.

## Current MVP features
- Local SQLite database; no internet permission and no cloud connection.
- Dashboard: today's and current month's income/expense; combined account balance.
- Income fields: date, source/from, amount, remark, received account.
- Expense fields: date, category, particular, amount, remark, paid-from account.
- Multiple bank accounts plus default Cash and UPI accounts; opening balance per account.
- Transfers between accounts do not count as income/expense.
- Carry-forward is derived from opening balance plus all transactions up to the selected/current date.
- Basic daily/monthly summaries and transaction history.

## Build APK with GitHub Actions
1. Upload the contents of this ZIP to the root of a GitHub repository (the `daily_accounts` folder must remain in the repository root).
2. Open the repository's **Actions** tab.
3. Select **Build Daily Accounts Manager APK** and click **Run workflow**, or push to `main`/`master`.
4. When the run finishes successfully, open the run and download the `DailyAccountsManager-debug-apk` artifact.
5. Extract the artifact ZIP to get `app-debug.apk` and install it on your Android phone.

## Important limitations — not yet production complete
- This is an initial source MVP, not a verified APK. Build/runtime tests have not been run in this environment.
- Backup/restore, edit/delete with ledger recalculation, PDF/CSV export, budget alerts, PIN lock, and advanced AI insights still need implementation/testing.
- Opening balances should be entered during initial setup before recording transactions. Do not change them later without reconciling.
- Monetary values are stored as REAL in this starter; for production accounting, use integer paise or decimal-safe arithmetic and add migration/tests.
