# scripts/ai_code_review.py
import anthropic
import os
import sys
import re
from github import Github

def analyze_java_code(file_content, file_path):
    """Java 코드를 분석하는 프롬프트 생성"""
    
    prompt = f"""
다음 Java 코드를 분석해주세요. 파일: {file_path}
```java
{file_content}
```

다음 3가지 관점에서 검토해주세요:

1. **네이밍 규칙 검증**:
   - 메소드명: camelCase, 동사로 시작 (get/set/is/has/create/update/delete 등)
   - 변수명: camelCase, 명사형, 의미있는 이름
   - 클래스명: PascalCase, 명사형
   - 상수명: UPPER_SNAKE_CASE
   - 일관성 없는 네이밍이 있다면 지적

2. **SOLID 원칙 준수**:
   - SRP: 단일 책임 원칙 위반 여부
   - OCP: 확장에 열려있고 수정에 닫혀있는지
   - LSP: 상속 관계가 적절한지
   - ISP: 인터페이스 분리가 적절한지
   - DIP: 의존성 역전이 적용되었는지
   - 느슨한 결합과 높은 응집도 확인
   - 불필요한 의존성이나 강한 결합 지적

   **DIP 분석 시 중요한 Spring/JPA 패턴 주의사항**:
   - Spring Data JPA의 Repository 인터페이스(`JpaRepository`, `CrudRepository`, `PagingAndSortingRepository` 등을 상속한 인터페이스)는 이미 인터페이스이므로 DIP를 준수하고 있음. 이를 DIP 위반으로 지적하지 말 것.
   - `@Repository` 어노테이션이 붙은 인터페이스, 또는 이름이 `~Repository`로 끝나는 인터페이스는 Spring Data JPA 패턴임을 인지할 것.
   - `@Service`, `@Component` 빈에 주입 시 인터페이스 타입으로 주입되는 경우는 DIP 준수로 판단할 것.
   - DIP 위반으로 지적할 실제 케이스: 구체 클래스(예: `new 구현체()` 직접 생성, 인터페이스 없이 구체 Service 클래스에 직접 의존)를 주입받는 경우만 해당함.

3. **SQL 쿼리 성능**:
   - N+1 쿼리 문제 가능성
   - 인덱스 누락 가능성이 있는 WHERE 조건
   - SELECT * 사용 여부
   - 과도한 JOIN 사용
   - 서브쿼리 최적화 가능 여부
   - 페이징 처리 누락
   - DISTINCT, GROUP BY 오용
   - 대량 데이터 처리 시 배치 처리 미적용

각 항목별로 구체적인 라인 번호와 함께 개선 제안을 해주세요.
문제가 없다면 "검토 완료: 문제 없음"이라고 답해주세요.
"""
    
    return prompt

def call_claude_api(prompt):
    """Claude API 호출"""
    client = anthropic.Anthropic(api_key=os.environ.get("ANTHROPIC_API_KEY"))
    
    message = client.messages.create(
        model="claude-sonnet-4-20250514",
        max_tokens=4000,
        messages=[
            {"role": "user", "content": prompt}
        ]
    )
    
    return message.content[0].text

def analyze_mybatis_xml(file_content, file_path):
    """MyBatis XML 쿼리 분석"""
    
    prompt = f"""
다음 MyBatis XML 매퍼 파일을 분석해주세요. 파일: {file_path}
```xml
{file_content}
```

**SQL 쿼리 성능 분석**:
1. 슬로우 쿼리 유발 가능성:
   - SELECT * 사용
   - 인덱스가 없을 것으로 예상되는 WHERE 조건
   - LIKE '%keyword%' 같은 전방 일치 검색
   - 과도한 JOIN (3개 이상)
   - IN 절에 너무 많은 값
   
2. N+1 문제 가능성:
   - resultMap의 association/collection 사용
   - 반복 호출될 수 있는 쿼리

3. 최적화 제안:
   - 인덱스 추가 권장 컬럼
   - 쿼리 재작성 제안
   - 페이징 처리 추가 필요 여부

구체적인 쿼리 ID와 함께 개선 제안을 해주세요.
"""
    
    return prompt

def post_review_comment(github_token, repo_name, pr_number, comments):
    """GitHub PR에 리뷰 코멘트 작성"""
    g = Github(github_token)
    repo = g.get_repo(repo_name)
    pr = repo.get_pull(pr_number)
    
    # 리뷰 본문 작성
    review_body = "## 🤖 AI 코드 리뷰 결과\n\n"
    review_body += comments
    
    pr.create_issue_comment(review_body)

def main():
    import argparse
    parser = argparse.ArgumentParser()
    parser.add_argument('--files', required=True)
    parser.add_argument('--pr-number', required=True, type=int)
    args = parser.parse_args()
    
    files = args.files.split()
    all_reviews = []
    
    for file_path in files:
        if not os.path.exists(file_path):
            continue
            
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # Java 파일 분석
        if file_path.endswith('.java'):
            prompt = analyze_java_code(content, file_path)
            review = call_claude_api(prompt)
            all_reviews.append(f"### 📄 {file_path}\n\n{review}\n\n")
        
        # MyBatis XML 분석
        elif file_path.endswith('.xml') and 'mapper' in file_path.lower():
            prompt = analyze_mybatis_xml(content, file_path)
            review = call_claude_api(prompt)
            all_reviews.append(f"### 📄 {file_path}\n\n{review}\n\n")
    
    if all_reviews:
        final_review = "\n".join(all_reviews)
        
        github_token = os.environ.get('GITHUB_TOKEN')
        repo_name = os.environ.get('GITHUB_REPOSITORY')
        
        post_review_comment(github_token, repo_name, args.pr_number, final_review)

if __name__ == '__main__':
    main()