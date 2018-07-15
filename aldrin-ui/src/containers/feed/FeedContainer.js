import React, { PureComponent } from 'react';
import { connect } from 'react-redux';
import ImmutablePropTypes from 'react-immutable-proptypes';
import { getFeedItems } from 'selectors/feed';
import FeedItem from "components/feed/FeedItem";
import { likePostItem } from 'actions/feed';
import PropTypes from 'prop-types';


class FeedContainer extends PureComponent {

    handleLikeClick = itemId => {
        this.props.likePostItem(itemId);
    };

    render() {
        const {
            items
        } = this.props;
        return (
            <div>
                {
                    items.map(item => (
                        <FeedItem
                            key={item.get('id')}
                            id={item.get('id')}
                            title={item.get('title')}
                            photoUrl={item.get('url')}
                            liked={item.get('liked')}
                            description={item.get('description')}
                            authorName={item.getIn(['author', 'name'])}
                            authorAvatar={item.getIn(['author', 'avatar'])}
                            onLikeClick={this.handleLikeClick}
                        />
                    ))
                }
            </div>
        );
    }
}

FeedContainer.propTypes = {
    items: ImmutablePropTypes.List,
    likePostItem: PropTypes.func.isRequired,
};

const mapStateToProps = state => ({
    items: getFeedItems(state),
});

export default connect(mapStateToProps, { likePostItem })(FeedContainer);