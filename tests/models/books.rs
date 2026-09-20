use book_club::{app::App, models::books};
use loco_rs::testing::prelude::*;
use sea_orm::{ActiveModelTrait, ActiveValue::Set, EntityTrait, SqlErr};
use serial_test::serial;

macro_rules! configure_insta {
    ($($expr:expr),*) => {
        let mut settings = insta::Settings::clone_current();
        settings.set_prepend_module_to_snapshot(false);
        let _guard = settings.bind_to_scope();
    };
}

#[tokio::test]
#[serial]
async fn test_model() {
    configure_insta!();

    let boot = boot_test::<App>().await.unwrap();
    seed::<App>(&boot.app_context).await.unwrap();

    // query your model, e.g.:
    //
    // let item = models::posts::Model::find_by_pid(
    //     &boot.app_context.db,
    //     "11111111-1111-1111-1111-111111111111",
    // )
    // .await;

    // snapshot the result:
    // assert_debug_snapshot!(item);
}

#[tokio::test]
#[serial]
async fn duplicate_book_url_is_rejected() {
    let db = given_seeded_db().await;
    let url = given_existing_book(&db).await;

    let duplicate = when_inserting_duplicate(&db, &url).await;

    then_duplicate_is_rejected(&db, duplicate).await;
}

async fn given_seeded_db() -> sea_orm::DatabaseConnection {
    let boot = boot_test::<App>().await.unwrap();
    seed::<App>(&boot.app_context).await.unwrap();
    boot.app_context.db.clone()
}

async fn given_existing_book(db: &sea_orm::DatabaseConnection) -> String {
    let url = "https://example.com/unique-url".to_string();
    books::ActiveModel {
        title: Set("First Book".to_string()),
        url: Set(url.clone()),
        ..Default::default()
    }
    .insert(db)
    .await
    .unwrap();
    url
}

async fn when_inserting_duplicate(
    db: &sea_orm::DatabaseConnection,
    url: &str,
) -> Result<books::Model, sea_orm::DbErr> {
    books::ActiveModel {
        title: Set("Second Book".to_string()),
        url: Set(url.to_string()),
        ..Default::default()
    }
    .insert(db)
    .await
}

async fn then_duplicate_is_rejected(
    db: &sea_orm::DatabaseConnection,
    duplicate: Result<books::Model, sea_orm::DbErr>,
) {
    assert!(
        matches!(
            duplicate.unwrap_err().sql_err(),
            Some(SqlErr::UniqueConstraintViolation { .. })
        ),
        "duplicate URL should violate the unique index"
    );
    assert_eq!(
        books::Entity::find().all(db).await.unwrap().len(),
        1,
        "duplicate insert should leave a single row"
    );
}
