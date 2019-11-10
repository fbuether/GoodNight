
package goodnight.common


class ApiPath(method: String, parts: Segment*)
    extends Path(method, parts : _*)
    with PathCreator
    with MethodExtractor
