import Sequelize from 'sequelize';

const sequelize = new Sequelize("auth-db", "admin", "123456", {
  host: "localhost",
  dialect: "postgres",
  quoteIdentifiers: false,
  define: {
    syncOnAssociation: true,
    timestamps: false,
    underscored: true,
    underscoreAll: true,
    freezeTableName: true,
  }
});

sequelize
.authenticate()
.then(() => {
  console.info("Connection has ben stablished!")
})
.catch(err => {
  console.error("unable to connect to the database")
  console.error(err.message)
})

export default sequelize;