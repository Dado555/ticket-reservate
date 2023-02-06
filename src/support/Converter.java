package support;

public interface  Converter<Entity1, Entity2> {

	Entity2 convert(Entity1 entity);
}
